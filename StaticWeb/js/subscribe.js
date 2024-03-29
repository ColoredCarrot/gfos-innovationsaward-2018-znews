/*
Add $.animateCss function to animate
an element once over a certain duration
and optionally call a callback on completion.
 */
$.fn.extend({
    animateCss: function(animationName, duration, onEnd)
    {
        if (duration)
            this.css({ '-webkit-animation-duration': duration, 'animation-duration': duration });
        this.addClass('animated ' + animationName).one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function()
        {
            $(this).removeClass('animated ' + animationName);
            if (onEnd)
                onEnd();
        });
        return this;
    }
});

/*
Intercept #subscribe-form.submit to call doSubscribe
 */
$(function()
{

    const EMAIL_REGEX = /^[a-z0-9%._-]+@[a-z0-9_-]+(\.[a-z0-9_-]+)*$/i;

    $('#subscribe-form').submit(function(e)
    {

        e.preventDefault();
        e.stopPropagation();

        const email = $('#sub-email').val();

        if (!EMAIL_REGEX.test(email))
            return;

        doSubscribe(email);

    });

});

/**
 * Called when subscribe action initiated.
 *
 * Fades out #subscribe-form, fades in #loading-img-div,
 * and makes an AJAX request to /api/subscribe,
 * handling success and error responses using swal
 * and fading #loading-im-div out and #check-your-email in.
 *
 * @param email The email address the user entered that they wish to be subscribed
 */
function doSubscribe(email)
{

    function handleSubscribeError(err)
    {
        swal("Error", "An unexpected error occurred. Please try again later", 'error');
    }

    function handleSubscribeResponse(data)
    {
        data = /*JSON.parse*/(data);

        if (data.success)
        {
            // Display "Check your Email!"-animation
            $('#loading-img-div').animateCss('fadeOut', '.4s', function()
            {
                $('#loading-img-div').css({ 'display': 'none' });
                $('#check-your-email').show().animateCss('rotateIn');
            });
            return;
        }

        let err = data.error;

        // TODO: remove error code from message. Currently there for debugging reasons
        swal('Error', err.message + ' (' + err.code + ')', 'error')
            .then(function()
            {
                // Clear and focus email input field
                $('#sub-email').val('').focus();
            });

    }

    // Display loading icon
    $('#subscribe-form').animateCss('fadeOut', '.4s', function()
    {
        $('#subscribe-form').css({ 'display': 'none' });
        $('#loading-img-div').show().animateCss('fadeIn', '.4s');
    });

    // Send data
    // FINDME: JS Access subscribe API here
    $.ajax('/api/subscribe', {
        data: {
            email: email// NOT encodeURIComponent(email) because that is automatically invoked and a double-call would in turn encode the %40 as %2540
        },
        cache: false,
        error: function(jqXHR, textStatus, errorThrown)
        {
            console.log(`Subscription error: ${textStatus} =>`);
            console.log(errorThrown);
            handleSubscribeError(errorThrown);
        },
        success: function(data, textStatus, jqXHR)
        {
            console.log(`Subscription success: ${textStatus} =>`);
            console.log(data);
            handleSubscribeResponse(data);
        }
    });

}
