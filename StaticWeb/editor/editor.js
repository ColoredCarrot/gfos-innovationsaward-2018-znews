$(function()
{

    // Set Showdown options
    // See https://github.com/showdownjs/showdown
    showdown.setOption('noHeaderId', true);
    showdown.setOption('simplifiedAutoLink', true);
    showdown.setOption('excludeTrailingPunctuationFromURLs', true);
    showdown.setOption('tables', true);
    showdown.setOption('headerLevelStart', 3);
    // This option should be turned off when generating html for display, not in the preview
    showdown.setOption('openLinksInNewWindow', true);

    showdown.setFlavor('github');

    let mkToHtmlConverter = new showdown.Converter({
        noHeaderId: true,
        simplifiedAutoLink: true,
        excludeTrailingPunctuationFromURLs: true,
        tables: true,
        headerLevelStart: 5,
        openLinksInNewWindow: true
    });

    /**
     * Renders a preview in $prtab
     * based on data retrieved from $mktab.
     */
    function renderPreview($prtab = $('#preview-tab'), $mktab = $('#markdown-tab'))
    {
        let $preview = $prtab.find('#preview');
        let mk = $mktab.find('#markdown').val();

        // Convert markdown to html (currently using Showdown)
        let html = mkToHtmlConverter.makeHtml(mk);

        // Wrap generated html in temporary div
        let $html = $('<div></div>').append($(html));

        // FINDME: Make elements negatively affected by Materialize browser-default
        $html.find('ul').addClass('browser-default');

        // Clear old preview and add markdown-generated html
        $preview.html('');
        $preview.append($html.children());

    }

    $('ul.tabs').tabs({ onShow: function(activeTab)
        {
            // Called upon switching to another tab
            let $activeTab = $(activeTab);
            if ($activeTab.attr('id') === 'preview-tab')
                renderPreview($activeTab);
        }});

});
