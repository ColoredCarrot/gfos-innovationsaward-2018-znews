jQuery(function($)
{

    function initButtons()
    {
        $('.edit-btn').click(function()
        {
            // Edit article (using /admin/edit_newsletter)
            let $card = $(this).parents('.article-card');
            act.cfg.useAttrs($card);
            act.onBtnEdit();
        });
        $('.delete-btn').click(function()
        {
            // Delete article
            let $card = $(this).parents('.article-card');
            act.cfg.useAttrs($card);
            act.onBtnDelete({ callback: () => $card.remove() });
        });
        $('.publish-btn').click(function()
        {
            // Publish article
            let $card = $(this).parents('.article-card');
            act.cfg.useAttrs($card);
            act.onBtnPublish({ callback: () => $card.find('.publish-btn').remove() });
        });

        $('#filter').on('input', function()
        {
            onFilterUpdate($(this).val());
        });
    }

    function onFilterUpdate(filter)
    {
        if (typeof filter === typeof '')
        {
            let origFilter = filter, filterString = filter.toLowerCase();
            filter = {
                matchRegistration: reg => reg.email.toLowerCase().includes(filterString),
                matchPublication: pub => pub.title.toLowerCase().includes(filterString)
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
