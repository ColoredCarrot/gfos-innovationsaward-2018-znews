$(function()
{

    const $progress = $('#progress-bar');
    $progress.parent().hide();

    function updateProgress(percent)
    {
        console.log('Update progress: ' + percent);
        $progress.css("width", percent + "%");
    }

    let actSaving = false;

    $('#act-save').click(function()
    {
        // Spam-click protection
        if (actSaving)
            return;

        // Display loading bar
        actSaving = true;
        updateProgress(1);
        $progress.parent().show();

        // TODO: Is a newsletter ID known?
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
                console.log("success");
                console.log(data);
                console.log(textStatus);
                console.log(jqXHR);
                updateProgress(100);
                window.setTimeout(function() { $progress.parent().hide(); actSaving = false }, 100);
                parent.displayGlobalToast('Saved Newsletter');
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
            status: {
                403: function()
                {
                    // TODO: handle not authenticated
                }
            }
        })

    });

});
