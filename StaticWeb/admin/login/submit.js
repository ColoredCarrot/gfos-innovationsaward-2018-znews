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
                swal({
                    title: 'Login Successful',
                    text: 'Click \'OK\' to proceed',
                    icon: 'success'
                }).then(function()
                {
                    // Redirect user after successful authentication to a main page
                    // TODO: Create main admin page
                    window.location.href = '/admin/index.html';
                });
            },
            statusCode: {
                403: function()
                {
                    // Authentication unsuccessful
                    swal('Login Unsuccessful', 'Invalid Username or Password', 'error')
                        .then(function()
                        {
                            $('#pw').val('').focus();
                        });
                }
            }
        });

        return false;

    });

});

