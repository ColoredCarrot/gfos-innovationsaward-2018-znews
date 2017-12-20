jQuery(function($)
{
    if (!$('main').length)
        $('body').children().appendTo($('<main></main>').appendTo($('body')))
});
