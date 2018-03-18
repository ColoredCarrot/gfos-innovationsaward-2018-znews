jQuery(function($)
{

    function initButtons()
    {
        $('#filter').on('input', function()
        {
            onFilterUpdate($(this).val());
        });
    }

    function onFilterUpdate(filter)
    {
        if (typeof filter === typeof '')
        {
            let filterStrings = filter.toLowerCase().split(' ');
            filter = {
                matchRegistration: reg => filterStrings.every(filterString => reg.email.toLowerCase().includes(filterString)),
                matchPublication: pub => filterStrings.every(filterString => pub.title.toLowerCase().includes(filterString))
            };
        }
        $('#col-regs').find('li[data-title]').each((idx, e) =>
        {
            let $e = $(e);
            let reg = {
                email: $e.attr('data-title')
            };
            if (filter.matchRegistration(reg))
                $e.show();
            else
                $e.hide();
        });
        $('#col-pubs').find('li[data-title]').each((idx, e) =>
        {
            let $e = $(e);
            let pub = {
                title: $e.attr('data-title')
            };
            if (filter.matchPublication(pub))
                $e.show();
            else
                $e.hide();
        });
    }

    initButtons();

});
