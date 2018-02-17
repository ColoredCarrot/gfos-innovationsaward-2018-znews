var ServerComm = (function()
{
    let sc = {
        onUpdateProgress: null,
        loginModal: {
            $loginModal: null,
            open: function(completeCallback)
            {
                if (completeCallback)
                    sc.loginModal.$loginModal.modal('open', {
                        complete: completeCallback
                    });
                else
                    sc.loginModal.$loginModal.modal('open');
            },
            close: function()
            {
                sc.loginModal.$loginModal.modal('close');
            }
        },
        swal: swal,
        toast: function()
        {
            throw new Error('toast not defined');
        },
        $: jQuery
    };

    sc.useProgressBar = function($progressBar)
    {
        let oldOnUpdateProgress = sc.onUpdateProgress;
        sc.onUpdateProgress = function(percentComplete)
        {
            if (oldOnUpdateProgress)
                oldOnUpdateProgress();
            else if (percentComplete)
            {
                $progressBar.css('width', percentComplete + '%')
                            .parent().show();
            }
            else
                $progressBar.parent().hide();
        }
    };

    let actSaving = false;
    sc.isSaving = () => actSaving;

    sc.doSave = function(parameters)
    {
        let { nid, newTitle, newText, newTags, nidConsumer } = parameters;

        if (actSaving)
            return false;
        actSaving = true;

        // Shorthand global functions
        let $ = sc.$,
            swal = sc.swal,
            toast = sc.toast;

        let updateProgress = function(percentComplete)
        {
            if (sc.onUpdateProgress)
                sc.onUpdateProgress(percentComplete);
            if (!percentComplete)
                actSaving = false;
        };

        function handleSaveResult(data)
        {
            data = JSON.parse(data);

            // Save successful
            if (data.success)
            {
                // Save returned newsletter ID
                if (nidConsumer)
                    nidConsumer(data.data.nid);
                window.setTimeout(() => updateProgress(0), 100);
                toast('Saved Newsletter');
            }
            else
            {
                swal('Internal Error', data.error.message, 'error')
                    .then(() => updateProgress(0));
            }

        }

        // Display loading bar
        updateProgress(1);

        let data = {};
        if (nid)
            data.nid = nid;
        if (newTitle)
            data.title = newTitle;
        if (newText)
            data.text = newText;
        if (newTags)
            data.tags = newTags;

        // Send XMLHttpRequest
        $.ajax('/admin/api/newsletter/save', {
            cache: false,
            data: data,
            success: function(data, textStatus, jqXHR)
            {
                updateProgress(100);
                handleSaveResult(data);
            },
            xhr: function()
            {
                let xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener('progress', function(evt)
                {
                    if (evt.lengthComputable)
                    {
                        // Upload progress update
                        let percentComplete = 100 * (evt.loaded / evt.total);
                        updateProgress(percentComplete / 2);
                    }
                }, false);

                xhr.addEventListener('progress', function(evt)
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
                    updateProgress(100);
                    swal('Error', 'You are not logged in. Do you want to log in now?', 'error', {
                        buttons: [true, true]
                    })
                        .then(function(doLoginNow)
                        {
                            if (doLoginNow)
                                displayLoginIFrame(parameters);
                            else
                                updateProgress(0);
                        });
                }
            }
        });

    };

    // TODO: For some reasons, clicks are not delegated to the iframe (Issue #3)
    let $iframe = $('<iframe id="login-iframe" src="/admin/login" frameborder="0" width="100%" height="100%"></iframe>');

    function displayLoginIFrame(doSaveParameters)
    {
        sc.loginModal.$loginModal.append($iframe);
        $iframe = sc.loginModal.$loginModal.children('#login-iframe');
        $iframe.on('load', function()
        {
            // IFrame reloaded
            const loadedUrl = $iframe[0].contentDocument.location.href;
            if (!loadedUrl.endsWith('admin/login'))
            {
                hideLoginIFrame();
                window.setTimeout(() => sc.doSave(doSaveParameters), 50);
            }
        });
        sc.loginModal.open(function()
        {
            // Called when modal closed
            sc.loginModal.$loginModal.find('#login-iframe').remove();
            // Reset progress bar
            (function updateProgress(percentComplete)
            {
                if (sc.onUpdateProgress)
                    sc.onUpdateProgress();
                if (!percentComplete)
                    actSaving = false;
            }(0));
        });
    }

    function hideLoginIFrame()
    {
        sc.loginModal.$loginModal.find('#login-iframe').remove();
        sc.loginModal.close();
        $iframe = $('<iframe id="login-iframe" src="/admin/login" frameborder="0" width="100%" height="100%"></iframe>');
    }

    return sc;
}());
