let DashboardSelect = (function(ds = {})
{

    let $dropdownText = $('#selected-dropdown-btn').find('span.badge');
    let nSelected = 0;

    ds.onToggleSelect = function($card = $('div.article-card:hover'))
    {
        nSelected += $card.find('input[type=checkbox]:checked').length ? 1 : -1;
        ds.updateDropdownText();
    };

    ds.updateDropdownText = function()
    {
        $dropdownText.text(nSelected.toString());
        if (nSelected === 1)
            $dropdownText.parent().removeClass('disabled');
        else if (!nSelected)
            $dropdownText.parent().addClass('disabled');
    };

    ds.getSelected = function()
    {
        return $('.article-card input[type=checkbox]:checked')
            .parents('.article-card')
            .map((idx, e) => $(e).attr('data-nid'))
            .get();
    };

    return ds;
})();

jQuery(function($)
{
    DashboardSelect.updateDropdownText();

    $('#selected-act-view').click(function()
    {
        // Open all selected articles in a new tab to compare

        let selected = DashboardSelect.getSelected();

        let docHeight = Math.max($(document).height(), $(window).height());
        let iframeHeight = docHeight / selected.length;

        let iframes = selected.map(e => `/view?nid=` + e)
                              .map(link => new URL(link, window.location.href).href)  // Convert to absolute
                              .map(link => `<iframe src="${link}" frameborder="0" width="100%" height="${iframeHeight}px"></iframe>`)
                              .join('');

        let dataUri = `data:text/html,<html><head><title>View Multiple Articles | ZNews</title></head><body style="margin: 0">${iframes}</body></html>`;

        let x = window.open();
        x.document.open();
        x.document.write("<iframe width='100%' height='100%' src='" + dataUri + "' frameborder='0'></iframe>");
        x.document.close();
    });

});
