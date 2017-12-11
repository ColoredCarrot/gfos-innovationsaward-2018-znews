/**
 * An extension for the standard server-comm.js module
 */

ServerComm = (function(sc)
{

    sc.doGetArticles = function(callback, amount = 5)
    {

        let $ = sc.$,
            swal = sc.swal;

        let updateProgress = function(percentComplete)
        {
            if (sc.onUpdateProgress)
                sc.onUpdateProgress(percentComplete);
        };

        function handleSaveResult(data)
        {
            data = JSON.parse(data);

            // Save successful
            if (data.success)
            {
                callback(data.data);
                updateProgress(0);
                console.log('Successfully retrieved articles', data);
            }
            else
            {
                swal('Error Retrieving Articles', data.error.message, 'error')
                    .then(() => updateProgress(0));
            }
        }

        updateProgress(1);

        // Send XMLHttpRequest
        $.ajax('/api/get', {
            cache: false,
            data: {
                amount: amount
            },
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
            }
        });

    };

    return sc;

}(ServerComm || {
    onUpdateProgress: null,
    swal: swal,
    $: jQuery
}));
