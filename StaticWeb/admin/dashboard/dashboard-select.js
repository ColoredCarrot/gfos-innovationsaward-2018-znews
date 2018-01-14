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

    return ds;
})();

jQuery(function($)
{
    DashboardSelect.updateDropdownText();
});
