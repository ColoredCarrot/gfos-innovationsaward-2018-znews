/*
Contains utility methods to bind to Editor action buttons
 */

var act = (function(act)
{

    act.cfg = act.cfg || {
        useAttrs: function($e)
        {
            act.cfg.nid   = $e.attr('data-nid');
            act.cfg.title = $e.attr('data-title');
            act.cfg.hash  = $e.attr('data-hash');
        }
    };

    act.onBtnEdit = function(nid = act.cfg.nid)
    {
        window.location.href = '/admin/edit_newsletter?nid=' + nid;
    };

    act.onBtnPublish = function(parameters)
    {
        let {
            nid = act.cfg.nid,
            title = act.cfg.title,
            hash = act.cfg.hash,
            callback = () => {}
        } = parameters;

        swal("Confirm Publication", `You are about to publish "${title}". It will be available for everyone to view! Do you wish to continue?`, 'warning', {
            buttons: true,
            dangerMode: true
        })
            .then(doPublish =>
            {
                return doPublish ? ajaxPublish(nid) : $.Deferred().reject('Cancelled');
            })
            .then(data =>
            {
                swal("Success", `You have published "${title}".`, 'success', {
                    buttons: {
                        cancel: "Close",
                        view: {
                            text: "View",
                            value: 'view'
                        }
                    }
                })
                    .then(action =>
                    {
                        action === 'view'
                            ? window.location.href = '/view?nid=' + nid
                            : callback();
                        /*if (action === 'view')
                            window.location.href = '/view?nid=' + nid;
                        else
                            callback();*/
                            //$card.find('.publish-btn').remove();
                    });
            }, errReason =>
            {
                if (errReason === 'Cancelled')
                    return;
                console.log("Internal Error", errReason);
                swal("Internal Error", "An unexpected error occurred. Please try again later.", 'error');
            });

        function ajaxPublish(nid)
        {
            return $.ajax('/admin/api/publish', {
                data: { nid: nid },
                cache: false
            });
        }
    };

    return act;
}({}));
