jQuery(function($)
{

    // TODO: Duplicate code (in /admin/edit_newsletter.js)
    function getQueryParamByName(name, url = window.location.href)
    {
        name = name.replace(/[\[\]]/g, "\\$&");
        const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
            results = regex.exec(url);
        if (!results)
            return null;
        if (!results[2])
            return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

    let nidParam = getQueryParamByName('nid');

    if (!nidParam)
    {
        swal("Error", "No Newsletter ID specified", 'error', {
            buttons: [true],
            closeOnClickOutside: false,
            closeOnEsc: false
        });
        return;
    }

    function renderText(text)
    {

        showdown.setFlavor('github');

        let mkToHtmlConverter = new showdown.Converter({
            noHeaderId: true,
            simplifiedAutoLink: true,
            excludeTrailingPunctuationFromURLs: true,
            tables: true,
            headerLevelStart: 4
        });

        // TODO: this is duplicate code, see /index/index.js
        // Convert markdown to html (currently using Showdown)
        let html = mkToHtmlConverter.makeHtml(text);
        // Wrap generated html in temporary div
        let $html = $('<div></div>').append($(html));
        // FINDME: Make elements negatively affected by Materialize browser-default
        $html.find('ul').addClass('browser-default');

        return $html;

    }

    $.ajax('/admin/api/view', {
        data: { nid: nidParam }
    })
        .then(data =>
        {
            data = JSON.parse(data);

            let article = data.data;

            $('#main-article-headline').text(article.title);
            $('#title').text(article.title + ' | ZNews');
            $('#main-container').append(renderText(article.text));

        }, error =>
        {
            if (error.status === 400)
            {
                // Invalid NID
                swal("Error", "Invalid Newsletter ID", 'error', {
                    buttons: [true],
                    closeOnClickOutside: false,
                    closeOnEsc: false
                });
            }
            else
            {
                swal("Internal Error", "An unexpected error occurred. Please try again later.", 'error', {
                    buttons: [true],
                    closeOnClickOutside: false,
                    closeOnEsc: false
                });
            }
        });

});
