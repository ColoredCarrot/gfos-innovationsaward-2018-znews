jQuery(function($)
{

    function stripHtmlTags(stringWithTags)
    {
        return $('<div/>').html(stringWithTags).text();
    }

    function trimString(string, toCharacters = 40)
    {
        return string.length > toCharacters ? string.substr(0, toCharacters - 1) + '…' : string;
    }

    function renderText(text)
    {

        showdown.setFlavor('github');

        let mkToHtmlConverter = new showdown.Converter({
            noHeaderId: true,
            simplifiedAutoLink: true,
            excludeTrailingPunctuationFromURLs: true,
            tables: true,
            headerLevelStart: 5
        });

        // TODO: this is (somewhat) duplicate code, see /editor/editor.js
        // Convert markdown to html (currently using Showdown)
        let html = mkToHtmlConverter.makeHtml(text);
        // Wrap generated html in temporary div
        let $html = $('<div></div>').append($(html));
        // FINDME: Make elements negatively affected by Materialize browser-default
        $html.find('ul').addClass('browser-default');

        return $html;

    }

    ServerComm.doGetArticles(function(data)
    {
        // Callback: Articles retrieved

        //<editor-fold desc="/*Old article templates...*/">
        // Old versions...
        // First: simple css
        /*let articleTemplate = `<div class="row">
            <div class="col s12">
                <h4 class="red-text text-lighten-1 article-headline"></h4>
            </div>
            <div class="col s12">
                <div class="article-text"></div>
            </div>
        </div>`;*/
        // Second: Cards
        /*let articleTemplate = `<div class="row">
            <div class="col s12">
                <div class="card">
                    <div class="card-content red-text">
                        <span class="card-title"></span>
                    </div>
                </div>
            </div>
        </div>`;*/
        //</editor-fold>

        let articleTemplate = `<li>
            <div class="collapsible-header">
                <div class="article-headline red-text"></div>
                <div class="article-preview"></div>
            </div>
            <div class="collapsible-body"></div>
        </li>`;

        let $articlesContainer = $('#articles-container');

        // For each article...
        $.each(data, function(idx, n)
        {
            let { title, text } = n;

            let $article = $(articleTemplate);
            let $html = renderText(text);
            let articlePreview = trimString(stripHtmlTags($html.prop('outerHTML')));

            $article.find('.article-headline').html(title);
            $article.find('.article-preview').append($('<div/>').text(articlePreview).prop('innerHTML'));
            $article.find('.collapsible-body').append($html.children());

            $articlesContainer.append($article);

        });

        // Open first article by default
        $articlesContainer.children().first().find('.collapsible-header').addClass('active');

        // Initialize Materialize collapsible
        $('.collapsible').collapsible({
            onOpen: function(el)
            {
                // Called when an article is opened
                let $this = $(el);
                $this.find('.article-preview').hide();
            },
            onClose: function(el)
            {
                // Called when an article is opened
                let $this = $(el);
                window.setTimeout(() => $this.find('.article-preview').show(), 120);
            }
        });

        /*
        TODO: On click of newsletter body, redirect to /view
        To do this, the NIDs need be stored as well,
        so the GetNewslettersResource must not require
        authentication to get NIDs.
         */
        /*$('.collapsible-body').click(function()
        {
            let $this = $(this);
            if ($this.parent().hasClass('active'))
            {
                window.location.href = '/view?nid=' + ;
            }
        });*/

    });

});
