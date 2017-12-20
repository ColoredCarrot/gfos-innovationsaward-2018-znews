jQuery(function($)
{
    let onResize = () => $('div.background').css('height', window.innerHeight + 'px');
    $(window).resize(onResize);
    onResize();

    $('img[src="/img/logo.jpg"]').click(() => window.location.href = '/');

    if (!$('main').length)
        $('body').children().appendTo($('<main></main>').appendTo($('body')));
});
