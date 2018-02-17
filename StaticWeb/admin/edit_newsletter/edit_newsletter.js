jQuery(function($)
{

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
    $editorFrame.on('load', () => $editorFrame.contents().find('#markdown').attr('tabindex', '2'));

    // TODO: We could provide a progress bar using ServerComm.useProgressBar($progressBar)

    ServerComm.loginModal.$loginModal = $('#login-modal');

    ((nidParam) =>
    {
        // If a 'nid' query param exists, load the newsletter
        if (!nidParam)
        {
            $('#edit-article-headline').text("Create Article");
            return;
        }
        $.ajax('/admin/api/by_nid', {
            async: true,  // TODO: disable functions until loaded/show preloader
            cache: false,
            data: {
                nid: nidParam
            },
            success: function(data, textStatus, jqXHR)
            {
                // Store retrieved data

                data = JSON.parse(data);

                if (!data.success)
                {
                    // TODO: Display actual error message
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

                let allKnownTags = ["banana", "apple", "Hello"]; // TODO: retrieve list of all known tags

                let tags = data.tags;
                $('#tags').material_chip({
                    data: tags.map(e => { return { tag: e }; }),
                    secondaryPlaceholder: "Tags",  // Materialize seems to swap placeholder and secondaryPlaceholder
                    placeholder: "Add Tag",
                    autocompleteOptions: {
                        data: allKnownTags.reduce(((res, e) => { return res[e] = null, res; }), {}),  // Cannot use comma expression in form ((res, e) => res[e] = null, res) because of UglifyJS. Must use braces
                        limit: Infinity,
                        minLength: 1
                    }
                });

                $('#ntitle').val(data.title);
                // If article is published, remove "Publish" button
                if (data.published)
                    $('#publish-btn').remove();
                Materialize.updateTextFields();

            }
        });
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
                }
            }
        }
        else
            saveData.nidConsumer = function(assignedNid)
            {
                $('#-data-nid-container').attr('data-nid', assignedNid);
            };

        ServerComm.doSave(saveData);

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

    // TODO: Register other action buttons here

});
