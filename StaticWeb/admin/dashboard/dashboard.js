jQuery(function($)
{

    let CARDS_IN_ONE_ROW = 4;

    function ajaxGetArticles(amount = 16)
    {
        return ServerComm.doGetArticles(data =>
        {
        }, amount, true);
    }

    function ajaxRowTemplate()
    {
        return $.ajax('/admin/dashboard/cards/row.html');
    }

    function ajaxCardTemplate()
    {
        return $.ajax('/admin/dashboard/cards/card.html');
    }

    $.when(ajaxGetArticles(), ajaxRowTemplate(), ajaxCardTemplate())
     .done(function(dataArticles, dataRow, dataCard)
     {
         // Called when all ajax requests complete

         // Convert string data to usable types
         let articles = JSON.parse(dataArticles[0]).data,
             $rowTemplate = $(dataRow[0]),
             $cardTemplate = $(dataCard[0]);

         let $container = $('#article-card-row-container');
         let $currentRow;

         $.each(articles, (idx, article) =>
         {

             // Add new row after e.g. 4 articles
             if (idx % CARDS_IN_ONE_ROW === 0)
             {
                 $currentRow = $rowTemplate.clone().appendTo($container);
             }

             let $card = $cardTemplate.clone();

             $card.find('.card-content').text(article.title);
             $card.find('.article-card').attr('data-nid', article.nid);

             $card.appendTo($currentRow);

         });

     })
     .fail(function(jqXHR, b, description)
     {
         if (description === 'Forbidden')
         {
             // Not logged in
             console.log("User not logged in: ", b, description);
             window.location.replace('/admin/login');
         }
         else
         {
             swal("Internal Error", `An unexpected error occured (${b}, ${description}). Please try again later.`);
         }
     });

});
