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
        },
        clear: function()
        {
            act.cfg.nid   = undefined;
            act.cfg.title = undefined;
            act.cfg.hash  = undefined;
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
            callback = () => {}
        } = parameters;

        swal("Confirm Publication", `You are about to publish "${title}". It will be available for everyone to view! Do you wish to continue?`, 'warning', {
            buttons: true,
            dangerMode: true
        })
            .then(doPublish => doPublish ? ajaxPublish(nid) : $.Deferred().reject('Cancelled'))
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

    act.onBtnDelete = function(parameters)
    {
        let {
            nid = act.cfg.nid,
            title = act.cfg.title,
            hash = act.cfg.hash,
            callback = () => {}
        } = parameters;

        // Display confirm dialog
        swal("Warning", `You are about to delete "${title}". Do you wish to proceed?`, 'warning', {
            buttons: [true, { closeModal: false }],
            dangerMode: true
        })
            .then(doDelete => doDelete ? ajaxDelete(nid, hash) : $.Deferred().reject('Cancelled'))
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
            .then(data =>
            {
                if (typeof data === typeof undefined)
                    return;
                data = /*JSON.parse*/(data);

                function handleSuccess()
                {
                    // Article was deleted successfully
                    return swal("Success", `Successfully deleted "${title}"`, 'success')
                        .then(() => callback());
                }

                function handleNotDeleted()
                {
                    return swal("Warning", "The article you are about to delete has been modified since this page was loaded. Do you wish to continue?", 'warning', {
                        buttons: [true, { closeModal: false }],
                        dangerMode: true
                    })
                        .then(doForceDelete => doForceDelete ? ajaxDelete(nid, undefined, true) : $.Deferred().reject('Cancelled'))
                        .then(data =>
                        {
                            data = /*JSON.parse*/(data);

                            if (!data.success)
                            {
                                console.log("Internal Error", data.error);
                                return swal("Internal Error", "An unexpected error occurred. Please try again later.", 'error');
                            }

                            return handleSuccess();

                        }, error =>
                        {
                            if (error === 'Cancelled')
                                return;

                            // Handle error (e.g. not logged in)
                            // TODO: Duplicate code (see above). => Extract to function

                            function handle403Forbidden()
                            {
                                swal("Error", "You are not logged in.", 'error')
                                    .then(() => window.location.href = '/admin/login');
                            }

                            switch (error.status)
                            {
                                case 403:
                                    return handle403Forbidden();
                                default:
                                    console.log(`Internal Error (${error.status})`, error);
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
            /*let data = { nid: nid };
            if (hash)
                data['hash'] = hash;
            if (force)
                data['force'] = force;*/
            return $.ajax('/admin/api/delete', {
                data: {
                    nid: nid,
                    hash: hash,
                    force: force || undefined
                },
                cache: false
            });
        }

    };

    return act;
}({}));
