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
            let $this = $(this);
            // Delete article
            let $card = $(this).parents('.article-card');
            act.cfg.useAttrs($card);
            act.onBtnDelete({
                callback: () =>
                {
                    $card.remove();
                }
            });
            //actDeleteArticle($this.parents('.article-card'));
        });
        $('.publish-btn').click(function()
        {
            // Publish article
            let $card = $(this).parents('.article-card');
            act.cfg.useAttrs($card);
            act.onBtnPublish({ callback: () =>
                {
                    $card.find('.publish-btn').remove();
                }});
        });
    }

    function actDeleteArticle($card, nid = $card.attr('data-nid'), title = $card.attr('data-title'))
    {
        // Display confirm dialog
        swal("Warning", "You are about to delete \"" + title + "\". Do you wish to proceed?", 'warning', {
            buttons: [true, { closeModal: false }],
            dangerMode: true
        })
            .then((doDelete) =>
            {
                if (!doDelete)
                {
                    return $.Deferred().reject('Cancelled');
                }
                return ajaxDelete(nid, $card.attr('data-hash'));
            })
            .catch(reason =>
            {
                if (reason === 'Cancelled')
                    return;

                // Handle error (e.g. not logged in)

                function handle403Forbidden()
                {
                    swal("Error", "You are not logged in.", 'error')
                        .then(() => window.location.href = '/admin/login');
                }

                switch (reason.status)
                {
                    case 403:
                        return handle403Forbidden();
                    default:
                        console.log("Internal Error (" + reason.status + ")", reason);
                        swal("Internal Error", "An unexpected error occurred. Please try again later.", 'error');
                }

            })
            //.then(ajaxResult => ajaxResult.json())
            .then(data =>
            {
                if (typeof data === typeof undefined)
                    return;
                data = JSON.parse(data);

                function handleSuccess()
                {
                    // Article was deleted successfully
                    return swal("Success", "Successfully deleted \"" + title + "\"", 'success')
                        .then(() =>
                        {
                            // Refresh page
                            $card.remove();
                        });
                }

                function handleNotDeleted()
                {
                    return swal("Warning", "The article you are about to delete has been modified since this page was loaded. Do you wish to continue?", 'warning', {
                        buttons: [true, { closeModal: false }],
                        dangerMode: true
                    })
                        .then(doForceDelete =>
                        {
                            if (!doForceDelete)
                                return $.Deferred().reject('Cancelled');
                            return ajaxDelete(nid, null, true);
                        })
                        .then(data =>
                        {
                            if (typeof data === typeof undefined)
                                return;
                            data = JSON.parse(data);

                            if (!data.success)
                            {
                                console.log("Internal Error", data.error);
                                return swal("Internal Error", "An unexpected error occurred. Please try again later.", 'error');
                            }

                            return handleSuccess();

                        })
                        .catch(reason =>
                        {
                            if (reason === 'Cancelled')
                                return;

                            // Handle error (e.g. not logged in)
                            // TODO: Duplicate code (see above). => Extract to function

                            function handle403Forbidden()
                            {
                                swal("Error", "You are not logged in.", 'error')
                                    .then(() => window.location.href = '/admin/login');
                            }

                            switch (reason.status)
                            {
                                case 403:
                                    return handle403Forbidden();
                                default:
                                    console.log("Internal Error (" + reason.status + ")", reason);
                                    swal("Internal Error", "An unexpected error occurred. Please try again later.", 'error');
                            }
                        });
                }

                if (!data.success)
                {
                    switch (data.error.code)
                    {
                        // Common.RS_ERR_NOT_DELETED
                        case 'NOT_DELETED':
                            return handleNotDeleted();
                        default:
                            console.log("Internal Error", data.error);
                            return swal("Internal Error", "An unexpected error occurred. Please try again later.", 'error');
                    }
                }

                handleSuccess();

            });

        function ajaxDelete(nid, hash, force = false)
        {
            let data = { nid: nid };
            if (hash)
                data['hash'] = hash;
            if (force)
                data['force'] = force;
            return $.ajax('/admin/api/delete', {
                data: data,
                cache: false
            });
        }
    }

});
