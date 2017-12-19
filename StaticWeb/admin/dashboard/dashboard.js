String.prototype.hashCode = String.prototype.hashCode || function()
{
    if (this.length === 0)
        return 0;
    let hash = 0,
        i,
        chr;
    for (i = 0; i < this.length; i++)
    {
        chr = this.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
};

jQuery(function($)
{

    let CARDS_IN_ONE_ROW = 4;

    function ajaxGetArticles(amount = 16)
    {
        return ServerComm.doGetArticles(data => {}, {
            amount: amount,
            includenid: true,
            includeNonPublished: true
        });
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
             $card.find('.article-card')
                  .attr('data-nid', article.nid)
                  .attr('data-title', article.title)
                  .attr('data-published', article.published)
                  .attr('data-hash', (article.title + article.text).hashCode());

             // If article is published, remove 'publish' button
             if (article.published)
                 $card.find('.publish-btn').remove();

             $card.appendTo($currentRow);

         });

         initButtons();

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

    function initButtons()
    {
        $('.edit-btn').click(function()
        {
            let $this = $(this);
            // Edit article
            let nid = $this.parents('.article-card').attr('data-nid');
            window.location.href = '/admin/edit_newsletter?nid=' + nid;
        });
        $('.delete-btn').click(function()
        {
            // Delete article
            let $card = $(this).parents('.article-card');
            act.cfg.useAttrs($card);
            act.onBtnDelete({ callback: () => $card.remove() });
        });
        $('.publish-btn').click(function()
        {
            // Publish article
            let $card = $(this).parents('.article-card');
            act.cfg.useAttrs($card);
            act.onBtnPublish({ callback: () => $card.find('.publish-btn').remove() });
        });
    }
});