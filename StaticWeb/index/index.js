jQuery(function($)
{

    String.prototype.escapeForJSON = function()
    {
        return this.replace(/\\n/g, '\\n')
                   .replace(/\\"/g, '\\"')
                   .replace(/\\r/g, '\\r')
                   .replace(/\\t/g, '\\t')
                   .replace(/\\b/g, '\\b')
                   .replace(/\\f/g, '\\f');
    };

    String.prototype.unescapeFromJSON = function()
    {
        return this.replace(/\\n/g, '\n')
                   .replace(/\\"/g, '"')
                   .replace(/\\r/g, '\r')
                   .replace(/\\t/g, '\t')
                   .replace(/\\b/g, '\b')
                   .replace(/\\f/g, '\f')
                   .replace(/\\\\/g, '\\');
    };

    function renderText(text)
    {

        showdown.setFlavor('github');

        let mkToHtmlConverter = new showdown.Converter({
            noHeaderId: true,
            simplifiedAutoLink: true,
            excludeTrailingPunctuationFromURLs: true,
            tables: true,
            headerLevelStart: 3
        });

        // TODO: this is (somewhat) duplicate code, see /editor/editor.js
        // Convert markdown to html (currently using Showdown)
        let html = mkToHtmlConverter.makeHtml(text.unescapeFromJSON());
        // Wrap generated html in temporary div
        let $html = $('<div></div>').append($(html));
        // FINDME: Make elements negatively affected by Materialize browser-default
        $html.find('ul').addClass('browser-default');

        return $html;

    }

    ServerComm.doGetArticles(function(data)
    {
        // Callback: Articles retrieved
        console.log(data);

        let articleTemplate = `<div class="row">
            <div class="col s12">
                <h4 class="red-text text-lighten-1 article-headline"></h4>
            </div>
            <div class="col s12">
                <div class="article-text"></div>
            </div>
        </div>`;

        let $articlesContainer = $('#articles-container');

        // For each article...
        $.each(data, function(idx, n)
        {
            let { title, text } = n;

            let $article = $(articleTemplate);
            let $html = renderText(text);

            $article.find('.article-headline').html(title);
            $article.find('.article-text').append($html.children());

            $articlesContainer.append($article);

        });

    });

});
