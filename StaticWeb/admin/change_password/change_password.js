jQuery(function($)
{

    window.setTimeout(() => $('#oldpw').focus(), 50);

    $('#main-form').submit(function(e)
    {
        e.preventDefault();
        e.stopPropagation();

        let oldpw = $('#oldpw').val().trim(),
            newpw = $('#newpw').val().trim(),
            confirmnewpw = $('#confirmnewpw').val().trim();

        if (!oldpw || !newpw || !confirmnewpw)
            return;

        if (newpw !== confirmnewpw)
        {
            swal("Error", "Passwords don't match", 'error');
            return;
        }

        $.ajax('/admin/api/change_password', {
            data: {
                oldpw: oldpw,
                newpw: newpw
            },
            dataType: 'json'
        })
         .then(data =>
         {
             if (!data.success)
             {
                 console.log("Error", data);
                 swal("Error", data.error.message, 'error');
                 return;
             }
             // success
             swal("Success", "Your password has been updated.", 'success')
                 .then(() => window.location.href = '/');
         }, error =>
         {
             if (error.status === 403)
             {
                 // Not logged in
                 swal("Error", "Cannot change password: You are not logged in.", 'error', {
                     buttons: {
                         cancel: "Return to main page",
                         confirm: "Log in"
                     }
                 })
                     .then(value =>
                     {
                         window.location.href = value ? '/admin/login' : '/';
                     });
             }
             else
             {
                 console.log("Internal Error: " + error.status, error);
                 swal("Internal Error", "An unexpected error occurred. Please try again later.", 'error');
             }
         });

    });

});
