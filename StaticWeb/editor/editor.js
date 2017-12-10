$(function()
{

    // Set Showdown options
    // See https://github.com/showdownjs/showdown
    showdown.setOption('noHeaderId', true);
    showdown.setOption('simplifiedAutoLink', true);
    showdown.setOption('excludeTrailingPunctuationFromURLs', true);
    showdown.setOption('tables', true);
    // This option should be turned off when generating html for display, not in the preview
    showdown.setOption('openLinksInNewWindow', true);

    showdown.setFlavor('github');

    let mkToHtmlConverter = new showdown.Converter();

    /**
     * Renders a preview in $prtab
     * based on data retrieved from $mktab.
     */
    function renderPreview($prtab = $('#preview-tab'), $mktab = $('#markdown-tab'))
    {
        let $preview = $prtab.find('#preview');
        let mk = $mktab.find('#markdown').val();

        // Ensure mkToHtmlConverter is a valid converter
        if (!mkToHtmlConverter)
            mkToHtmlConverter = new showdown.Converter();

        // Convert markdown to html (currently using Showdown)
        let html = mkToHtmlConverter.makeHtml(mk);

        // Wrap generated html in div for later recursive iteration
        let $html = $('<div></div>').append($(html));

        // Recursively walk generated html, adding .browser-default to all
        function walker($e)
        {
            $e.children().each(function()
            {
                let $this = $(this);
                $this.addClass('browser-default');
                walker($this);
            });
        }
        walker($html);

        // Clear old preview and add markdown-generated html
        $preview.html('');
        $html.children().each(function(idx, $e)
        {
            $preview.append($e);
        });

    }

    $('ul.tabs').tabs({ onShow: function(activeTab)
        {
            // Called upon switching to another tab
            let $activeTab = $(activeTab);
            if ($activeTab.attr('id') === 'preview-tab')
                renderPreview($activeTab);
        }});

});
