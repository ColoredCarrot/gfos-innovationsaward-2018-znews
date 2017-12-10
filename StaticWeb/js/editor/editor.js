$(function()
{

    /**
     * Renders a preview in $prtab
     * based on data retrieved from $mktab.
     */
    function renderPreview($prtab = $('#preview-tab'), $mktab = $('#markdown-tab'))
    {



    }

    $('ul.tabs').tabs({ onShow: function(activeTab)
        {
            // Called upon switching to another tab
            let $activeTab = $(activeTab);
            if ($activeTab.attr('id') === 'preview-tab')
                renderPreview($activeTab);
        }});

});
