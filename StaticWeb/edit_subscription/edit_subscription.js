jQuery(function($)
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

    function makeRequest(email)
    {
        return $.ajax('/edit_subscription_data', {
            data: { email: email },
            dataType: 'json',
            method: 'post'
        });
    }

    function handleSuccessfulRequest(data, statusText, jqXHR)
    {
        console.log("request successful received; ", data, " ; ", jqXHR);
    }

    function handleUnsuccessfulRequest(jqXHR)
    {
        console.error("request unsuccessful; ", jqXHR);
    }

    /**
     * Quick and dirty email validation;
     *  not to be relied on
     */
    function validateEmail(email)
    {
        return email.length >= 3 && /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(email);
    }

    function displayEmailPrompt(invalidEmail = false)
    {
        return swal({
            title: "Enter email address",
            content: $(`<input id="email-input" type="email" placeholder="Email address" class="validate${invalidEmail ? " invalid" : ""}">`)[0],
            buttons: {
                cancel: true,
                confirm: {
                    text: "Submit",
                    closeModal: false
                }
            },
            closeOnClickOutside: false,
            closeOnEsc: false
        });
    }

    function displayAndHandleEmailPrompt(a = displayEmailPrompt())
    {
        return a.then(email =>
        {
            if (email === null)
            {
                // Cancel
                window.location.href = '/';
                return new Promise(((resolve, reject) => {}));  // Return promise that will never be resolved or rejected
            }
            email = $('#email-input').val();
            if (!validateEmail(email))
                return displayAndHandleEmailPrompt(displayEmailPrompt(true));
            return makeRequest(email);
        });
    }

    function init()
    {
        let email = getQueryParamByName('email');
        (email
            ? Promise.resolve(email)
            : displayAndHandleEmailPrompt())
            /*.then(email =>
            {
                if (email === null)
                {
                    // Cancel
                    window.location.href = '/';
                    return new Promise(((resolve, reject) => {}));  // Return promise that will never be resolved or rejected
                }
                if (!validateEmail(email))
                    throw 'invalid_email';
                return makeRequest(email);
            })*/
            .then(data =>
            {
                // Request successful; data is array of tags (strings)
                // TODO: Display tags

            }, error =>
            {
                if (error === 'invalid_email')
                {
                    //swal();
                }
                window.console.error(error);
            });
        /*let email = getQueryParamByName('email');
        if (!email)
        {
            swal("Error", "No email parameter specified", 'error', {
                closeOnClickOutside: false,
                closeOnEsc: false,
                buttons: {}
            });
            return;
        }
        makeRequest(email).then(handleSuccessfulRequest, handleUnsuccessfulRequest);*/
    }

    init();

});
