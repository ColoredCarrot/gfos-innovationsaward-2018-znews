jQuery(function($)
{

    let EMAIL,
        OTHER_TAGS,
        SUBSCRIBED_TAGS;

    /*
    TODO: Add Drag n' Drop-support
     */

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
        EMAIL = email;
        return $.ajax('/edit_subscription_data', {
            data: { email: email },
            dataType: 'json',
            method: 'post'
        });
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
            content: $(`<input id="email-input" type="email" placeholder="Email address" class="validate${invalidEmail ? " invalid" : ""}">`).on('keyup', function(evt)
            {
                if (evt.keyCode === 13)
                {
                    evt.stopPropagation();
                    evt.preventDefault();
                    $('.swal-button--confirm').click();  // This cannot be the best way
                }
            })[0],
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
            ? makeRequest(email)
            : displayAndHandleEmailPrompt())
            .then(data =>
            {
                // Request successful; data is of form { subscribed: ["a", "b"], other_known: ["c", "d"] }
                // Display tags

                updateData(data.subscribed, data.other_known);

                swal.stopLoading();
                swal.close();

            }, error =>
            {
                window.console.error(error);
                swal("Error", "You have specified an invalid email address.", 'error', {
                    buttons: [true, "Retry"]
                })
                    .then(value =>
                    {
                        if (value)
                            // Retry
                            init();
                        else
                            window.location.href = '/';
                    });
            });
    }

    init();

    function actionRemoveTag($collectItem)
    {
        // Make request, remove button (TODO: Replace with loading icon)
        let tag = $collectItem.attr('data-tag');
        $.ajax('/edit_subscription_data', {
            method: 'post',
            data: {
                email: EMAIL,
                unsubscribe_tag: tag
            },
            dataType: 'json'
        })
         .then(function success(data)
         {
             // data in form { subscribed: ["a", "b"], other_known: ["c", "d"] }
             // TODO: Add consistency check with expected values
             // Always display what the server returned, not what we have client-side
             updateData(data.subscribed, data.other_known);
         }, function error(jqXHR, textStatus)
         {
             console.error(jqXHR, textStatus);
             if (jqXHR.status === 404)
             {
                 // TODO: Handle 404
             }
         });
    }

    function actionAddTag($collectItem)
    {
        // Make request, remove button (TODO: Replace with loading icon)
        let tag = $collectItem.attr('data-tag');
        $.ajax('/edit_subscription_data', {
            method: 'post',
            data: {
                email: EMAIL,
                subscribe_tag: tag
            },
            dataType: 'json'
        })
         .then(function success(data)
         {
             // data in form { subscribed: ["a", "b"], other_known: ["c", "d"] }
             // TODO: Add consistency check with expected values
             // Always display what the server returned, not what we have client-side
             updateData(data.subscribed, data.other_known);
         }, function error(jqXHR, textStatus)
         {
             console.error(jqXHR, textStatus);
             if (jqXHR.status === 404)
             {
                 // TODO: Handle 404
             }
         });
    }

    function updateData(subscribedTags = SUBSCRIBED_TAGS, otherTags = OTHER_TAGS)
    {
        SUBSCRIBED_TAGS = subscribedTags;
        OTHER_TAGS = otherTags;

        let SUBSCRIBED_TEMPLATE = '<li class="collection-item" data-tag="{tag}"><div>{tag}<a class="secondary-content flow-text" style="padding: 2px; zoom: 1.2; cursor: pointer; margin-top: -3px">&times;</a></div></li>';
        let AVAILABLE_TEMPLATE = '<li class="collection-item" data-tag="{tag}"><div>{tag}<a class="secondary-content flow-text" style="padding: 2px; zoom: 1.2; cursor: pointer; margin-top: -3px">+</a></div></li>';

        let $collection = $('#subscribed-tags');

        $collection.empty();
        subscribedTags.map(tag => $(SUBSCRIBED_TEMPLATE.replace(/{tag}/g, tag))
            .appendTo($collection)
            .find('a').click(function()
            {
                actionRemoveTag($(this).parents('li'));
            }));

        $collection = $('#available-tags');

        $collection.empty();
        otherTags.map(tag => $(AVAILABLE_TEMPLATE.replace(/{tag}/g, tag))
            .appendTo($collection)
            .find('a').click(function()
            {
                actionAddTag($(this).parents('li'));
            }));

    }

});
