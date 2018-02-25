jQuery(function($)
{

    let KNOWN_TAGS = [];

    function getQueryParamByName(name, url = window.location.href)
    {
        name = name.replace(/[\[\]]/g, "\\$&");
        const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
            results = regex.exec(url);
        if (!results)
            return null;
        if (!results[2])
            return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

    if (typeof ServerComm === typeof undefined || !ServerComm)
        throw new Error("ServerComm not defined! Please include `server-comm.js` before `edit_newsletter.js`");

    ServerComm.toast = function(message, duration = 2000)
    {
        Materialize.toast(message, duration)
    };

    // Change tab focus order. Must be done after iframe has loaded
    let $editorFrame = $('#editor-frame');

    // TODO: We could provide a progress bar using ServerComm.useProgressBar($progressBar)

    ServerComm.loginModal.$loginModal = $('#login-modal');

    ((nidParam) =>
    {
        // If a 'nid' query param exists, load the newsletter
        if (!nidParam)
        {
            $('#edit-article-headline').text("Create Article");
            $.ajax('/list_tags', {
                async: true,
                cache: false,  // TODO: Maybe we should cache?
                data: {
                    limit: -1  // No limit
                },
                success: function(allKnownTags, statusText, jqXHR)
                {
                    // allKnownTags is array of tags
                    KNOWN_TAGS = /*JSON.parse*/(allKnownTags);
                    $('#tags').material_chip({
                        secondaryPlaceholder: "Tags",  // Materialize seems to swap placeholder and secondaryPlaceholder
                        placeholder: "Add tag...",
                        autocompleteOptions: {
                            data: KNOWN_TAGS.reduce(((res, e) => { return res[e] = null, res; }), {}),  // Cannot use comma expression in form ((res, e) => res[e] = null, res) because of UglifyJS. Must use braces
                            limit: Infinity,
                            minLength: 1
                        }
                    });
                }
            });
            return;
        }

        $.when(
            $.ajax('/admin/api/by_nid', {
                async: true,  // TODO: disable functions until loaded/show preloader
                cache: false,
                data: {
                    nid: nidParam
                }
            }), $.ajax('/list_tags', {
                async: true,
                cache: false,  // TODO: Maybe we should cache?
                data: {
                    limit: -1  // No limit
                }
            })
        ).then(
            function success(a1, a2)
            {
                // a1 and a2 are of the form [data, textStatus, jqXHR]

                let data = /*JSON.parse*/(a1[0]);
                KNOWN_TAGS = /*JSON.parse*/(a2[0]);

                if (!data.success)
                {
                    // TODO: Display actual error message data.error.message
                    window.location.replace('/404');
                    return;
                }

                data = data.data;

                $('#-data-nid-container').attr('data-nid', nidParam);
                // As soon as editor frame is loaded, init text area

                $('#editor-frame').on('load', () =>
                {
                    $('#editor-frame').contents().find('#markdown').val(data.text);
                    dirty_hash.recompute();
                });

                let tags = data.tags;
                $('#tags').material_chip({
                    data: tags.map(e => { return { tag: e }; }),
                    secondaryPlaceholder: "Tags",  // Materialize seems to swap placeholder and secondaryPlaceholder
                    placeholder: "Add tag...",
                    autocompleteOptions: {
                        data: KNOWN_TAGS.reduce(((res, e) => { return res[e] = null, res; }), {}),  // Cannot use comma expression in form ((res, e) => res[e] = null, res) because of UglifyJS. Must use braces
                        limit: Infinity,
                        minLength: 1
                    }
                });

                $('#ntitle').val(data.title);
                // TODO: Add another indicator.  If article is published, remove "Publish" button
                if (data.published)
                    $('#publish-btn').remove();
                Materialize.updateTextFields();

                dirty_hash.allowDisplayWarning = true;

            },
            function error(jqXHR)
            {
                console.error(jqXHR);
                if (jqXHR.status === 403)
                {
                    // Not logged in; session expired
                    swal("Session Expired", "You are not logged in. Click \"Continue\" to log in.", 'error',
                        {
                            closeOnClickOutside: false,
                            closeOnEsc: false,
                            buttons: {
                                confirm: "Continue"
                            }
                        })
                        .then(() => window.location.href = '/admin/login?target=' + encodeURIComponent('/admin/edit_newsletter?nid=' + nidParam));
                    return;
                }
                swal("Internal Error", "An unexpected error occurred. Please see the console for more details or try again later.", 'error',
                    {
                        buttons: {
                            retry: {
                                text: "Try Again",
                                value: 'retry'
                            },
                            exit: {
                                text: "Exit",
                                value: 'exit'
                            }
                        },
                        closeOnClickOutside: false,
                        closeOnEsc: false
                    })
                    .then(value =>
                    {
                        switch (value)
                        {
                            case 'retry':
                                window.location.reload(true);  // Forced reload
                                break;
                            case 'exit':
                                window.location.href = '/';
                                break;
                        }
                    });
            }
        );
    })(getQueryParamByName('nid'));

    $('#save-btn').click(function()
    {

        // Get newsletter ID
        let nid = $('#-data-nid-container').attr('data-nid');
        nid = typeof nid !== typeof undefined && nid !== false ? nid : null;

        if (!$('#ntitle').val().trim())
        {
            swal("Error", "You must specify a title in order to save this newsletter.", 'error')
                .then(() => $('#ntitle').focus());
            return;
        }

        let saveData = {
            newTitle: $('#ntitle').val(),
            newText: $editorFrame.contents().find('#markdown').val()
        };

        // Add tags to saveData
        let tags = $('#tags').material_chip('data');  // [{tag:"a"},{tag:"b"}]
        tags = tags.reduce((res, e) => { return res.push(e.tag), res; }, []);  // ["a", "b"]
        saveData.newTags = tags;

        // Check if any unknown tags would be saved and warn
        let unknownTags = tags.filter(tag => !KNOWN_TAGS.includes(tag));

        (unknownTags.length
            ? swal("Warning", `Please confirm you wish to create these unknown tags: ${unknownTags.join(", ")}`, 'warning', {
                buttons: true
            })
            : Promise.resolve(true))
            .then(confirm =>
        {
            if (!confirm)
                return;

            if (nid)
            {
                saveData.nid = nid;
                saveData.nidConsumer = function(assignedNid)
                {
                    if (assignedNid !== nid)
                    {
                        swal("Internal Error", "Returned assigned newsletter ID does not match stored ID", 'error')
                            .then(() => { throw new Error("INTERNAL ERROR: Returned assigned newsletter ID does not match stored ID") });
                    }
                    else
                    {
                        // Success
                        dirty_hash.recompute();
                        KNOWN_TAGS = KNOWN_TAGS.concat(unknownTags);
                    }
                }
            }
            else
                saveData.nidConsumer = function(assignedNid)
                {
                    $('#-data-nid-container').attr('data-nid', assignedNid);
                };

            // Make request to server
            ServerComm.doSave(saveData);

        });

    });

    $('#publish-btn').click(function()
    {
        let nid = $('#-data-nid-container').attr('data-nid');

        // Abort if article is not saved
        if (typeof nid === typeof undefined || !nid)
        {
            console.log("WARNING: #publish-btn clicked but no data-nid known");
            return;
        }

        act.cfg.clear();
        act.cfg.nid = nid;
        act.cfg.title = $('#ntitle').val();

        act.onBtnPublish({
            callback: () => $('#publish-btn').remove()
        });

    });

    $('#delete-btn').click(function()
    {
        let nid = $('#-data-nid-container').attr('data-nid');

        // Abort if article is not saved
        if (typeof nid === typeof undefined || !nid)
        {
            swal("Error", "This article is not saved and can therefore not be deleted.", 'error');
            return;
        }

        act.cfg.clear();
        act.cfg.nid = nid;
        act.cfg.title = $('#ntitle').val();

        act.onBtnDelete({
            callback: () => window.location.href = '/admin/dashboard'
        });
    });

    $('#view-btn').click(function()
    {
        let nid = $('#-data-nid-container').attr('data-nid');

        // Abort if article is not saved
        if (typeof nid === typeof undefined || !nid)
        {
            swal("Error", "This article is not saved and can therefore not be viewed.", 'error');
            return;
        }

        window.location.href = '/view?nid=' + nid;
    });

    // TODO: Register other action buttons here

});
