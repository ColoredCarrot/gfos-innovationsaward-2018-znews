var CommonSwals = (function(cs)
{

    cs.notLoggedIn = function(options)
    {

        let {
            suggestLogin = true,
            forceLogin,
            loginTarget,
            encodeLoginTarget = true
        } = options;

        let loginNow = (() => window.location.href = loginTarget ? '/admin/login?target=' + (encodeLoginTarget ? encodeURIComponent(loginTarget) : loginTarget) : '/admin/login');

        if (forceLogin)
            swal("Session Expired", "You are not logged in. Click \"Continue\" to log in.", 'error',
                {
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    buttons: {
                        confirm: "Continue"
                    }
                })
                .then(loginNow);
        else if (suggestLogin)
            return swal("Error", "You are not logged in. Do you want to log in now?", 'error', {
                buttons: [true, true]
            })
                .then(function(doLoginNow)
                {
                    if (doLoginNow)
                        loginNow();
                    return Promise.resolve();
                });
        else
            return swal("Error", "You are not logged in.", 'error', {
                buttons: ["Close"]
            });

    };

    cs.internalError = function(options)
    {
        let {
            title = "Internal Error",
            secondaryTitle,
            detailMessage = "An unexpected error occurred. Please try again later."
        } = options;
        return swal(title + secondaryTitle ? (": " + secondaryTitle) : '', detailMessage, 'error');
    };

    return cs;
})(CommonSwals || {});
