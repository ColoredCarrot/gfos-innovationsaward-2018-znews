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

function doSubscribe(email)
{

    // Display "Check your Email!"-animation
    $('#subscribe-form').animateCss('fadeOut', '.4s', function()
    {
        $('#subscribe-form').css({ 'display': 'none' });
        $('#check-your-email').show().animateCss('rotateIn');
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
            console.log('Error: ' + textStatus);
            console.log(errorThrown)
        },
        success: function(data, textStatus, jqXHR)
        {
            console.log('Success: ' + textStatus);
            console.log(data);
        }
    });

}
