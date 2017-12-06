$(function()
{

    $('#main-form').submit(function(e)
    {
        e.preventDefault();
        e.stopPropagation();

        var data = {
            usr: $('#usr').val(),
            pw: $('#pw').val()
        };

        $.ajax('/api/v1/admin/get_token', {
            cache: false,
            data: data,
            method: 'POST',
            success: function(data, textStatus, jqXHR)
            {
                // TODO: handle authentication successful
                // redirect user to some sort of success page maybe?
                // or an admin management site?
                console.log("success");
                console.log(data);
                console.log(textStatus);
                console.log(jqXHR);
            },
            statusCode: {
                403: function()
                {
                    // TODO: handle authentication unsuccessful
                    console.log('403');
                }
            }
        });

        return false;

    });

});

