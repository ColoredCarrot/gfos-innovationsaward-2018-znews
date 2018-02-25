$(function()
{

    //const swal = parent.globalSwal;

    const $progress = $('#progress-bar');
    $progress.parent().hide();

    function updateProgress(percent)
    {
        console.log('Update progress: ' + percent);
        $progress.css("width", percent + "%");
    }

    let actSaving = false;

    function handleSaveResult(data)
    {
        data = /*JSON.parse*/(data);
        updateProgress(100);

        // Save successful
        if (data.success)
        {
            window.setTimeout(function()
            {
                $progress.parent().hide();
                actSaving = false
            }, 100);
            parent.displayGlobalToast('Saved Newsletter');
            return
        }

        const err = data.error;

        // Must be in parent window
        parent.globalSwal('Internal Error', err.message, 'error')
            .then(function()
            {
                $progress.parent().hide();
                actSaving = false;
            });

    }

    // For some reason, using let instead of var/const does not work
    // I (@ColoredCarrot) suspect the function is executed within
    // the parent context after successful iframe login and is not defined there
    // because let has a more limited scope
    const onClickSave = function()
    {
        // Spam-click protection
        if (actSaving)
            return;

        // Display loading bar
        actSaving = true;
        updateProgress(1);
        $progress.parent().show();

        // TO/DO: Is a newsletter ID known?
        const nid = parent.getNidIfKnown();

        let data = {};
        if (nid)
            data['nid'] = nid;

        // Send XMLHttpRequest
        $.ajax('/admin/api/newsletter/save', {
            cache: false,
            data: data,
            success: function(data, textStatus, jqXHR)
            {
                handleSaveResult(data);
                /*console.log("success");
                console.log(data);
                console.log(textStatus);
                console.log(jqXHR);*/
            },
            xhr: function()
            {
                let xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener("progress", function(evt)
                {
                    if (evt.lengthComputable)
                    {
                        // Upload progress update
                        let percentComplete = 100 * (evt.loaded / evt.total);
                        updateProgress(percentComplete / 2);
                    }
                }, false);

                xhr.addEventListener("progress", function(evt)
                {
                    if (evt.lengthComputable)
                    {
                        // Download progress update
                        let percentComplete = 100 * (evt.loaded / evt.total);
                        updateProgress(50 + percentComplete / 2);
                    }
                }, false);

                return xhr;
            },
            statusCode: {
                403: function()
                {
                    parent.notLoggedInSwal()
                        .then(function(doLoginNow)
                        {
                            $progress.parent().hide();
                            actSaving = false;
                            if (doLoginNow)
                                displayLoginIFrame();
                        });
                }
            }
        })

    };
    $('#act-save').click(onClickSave);

    // TODO: For some reasons, clicks are not delegated to the iframe (Issue #3)
    let $iframe = $('<iframe id="login-iframe" src="/admin/login" frameborder="0" width="100%" height="100%"></iframe>');

    function displayLoginIFrame()
    {
        parent.LoginModal.get$().append($iframe);
        $iframe = parent.LoginModal.get$().children('#login-iframe');
        $iframe.on('load', function()
        {
            // IFrame reloaded
            const loadedUrl = $iframe[0].contentDocument.location.href;
            if (!loadedUrl.endsWith('admin/login'))
            {
                hideLoginIFrame();
                onClickSave();
            }
        });
        parent.LoginModal.open(function()
        {
            // Called when modal closed
            parent.LoginModal.get$().find('#login-iframe').remove();
        });
    }

    function hideLoginIFrame()
    {
        parent.LoginModal.get$().find('#login-iframe').remove();
        parent.LoginModal.close();
        $iframe = $('<iframe id="login-iframe" src="/admin/login" frameborder="0" width="100%" height="100%"></iframe>');
    }

});
