jQuery(function($)
{

    $.ajax('/admin/api/logout', {
        cache: false,
        dataType: 'json'
    })
        .then(data =>
        {
            if (!data.success)
            {
                console.log("Internal Error", data);
                handleInternalError();
            }
        }, error =>
        {
            if (error.status === 403)
            {
                // Not logged in in the first place
                window.location.replace('/admin/login');
            }
            else
            {
                console.log("Internal Error: " + error.status, error);
                handleInternalError();
            }
        });

    function handleInternalError()
    {
        $('div.foreground > div').hide();
        alert("Internal Error. See the console for more details");
    }

});
