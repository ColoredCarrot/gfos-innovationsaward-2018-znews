jQuery(function($)
{
    let onResize = () => $('div.background').css('height', window.innerHeight + 'px');
    $(window).resize(onResize);
    onResize();
});
