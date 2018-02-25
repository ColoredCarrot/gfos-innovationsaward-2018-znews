$(function()
{

    // TODO: Duplicate code (in /admin/edit_newsletter.js)
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

    window.setTimeout(() => $('#usr').focus(), 50);

    $('#main-form').submit(function(e)
    {
        e.preventDefault();
        e.stopPropagation();

        let data = {
            email: $('#usr').val(),
            password: $('#pw').val()
        };

        $.ajax('/admin/api/get_token', {
            cache: false,
            data: data,
            method: 'POST',
            success: function(data, textStatus, jqXHR)
            {
                // Authentication successful
                swal({
                    title: "Login Successful",
                    text: "Click 'OK' to proceed",
                    icon: 'success'
                }).then(function()
                {
                    // Redirect user to a main admin page
                    let target = getQueryParamByName('target');
                    window.location.href = target || '/admin/index';
                });
            },
            statusCode: {
                403: function()
                {
                    // Authentication unsuccessful
                    swal("Login Unsuccessful", "Invalid E-Mail or Password", 'error')
                        .then(function()
                        {
                            // Clear and focus password field
                            $('#pw').val('').focus();
                        });
                }
            }
        });

        return false;

    });

});

