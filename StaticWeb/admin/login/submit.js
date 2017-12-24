$(function()
{

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
                    window.location.href = '/admin/index';
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

