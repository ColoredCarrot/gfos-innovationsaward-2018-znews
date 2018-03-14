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

    let textToHtmlRendererQuill;
    let textToHtmlRendererVirtContainer;

    function renderText(text)
    {
        return DeltaRenderer.renderToHTML(text);
    }

    $.ajax('/admin/api/view', {
        data: { nid: nidParam }
    })
     .then(data =>
     {
         data = /*JSON.parse*/(data);

         let article = data.data;

         if (article.datePublished)
            article.datePublished = new Date(article.datePublished);

         $('#main-article-headline').text(article.title);
         $('#title').text(article.title + ' | ZNews');
         $('#main-container').append($(renderText(article.the_delta)));

         if (article.publisher)
             $('#description').html(`Published by ${article.publisher} on ${formatDate(article.datePublished)}. <a class="go-back-link" href="javascript:window.history.back()">Go back</a>`);
         else
         {
             $('#description').html(`This article has not yet been published. You are viewing a draft version. <a class="go-back-link" href="/admin/dashboard">Go back</a>`)
                              .parent()
                              .removeClass('grey')
                              .addClass('yellow');
         }

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

    function formatDate(date)
    {
        const monthNames = [
            "January", "February", "March",
            "April", "May", "June", "July",
            "August", "September", "October",
            "November", "December"
        ];

        let day = date.getDate(),
            monthIndex = date.getMonth(),
            year = date.getFullYear();

        return `${monthNames[monthIndex]} ${day}, ${year}`;
    }

});
