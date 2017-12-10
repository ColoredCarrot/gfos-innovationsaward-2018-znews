jQuery(function($)
{
    if (!ServerComm)
        throw new Error('ServerComm not defined! Please include `server-comm.js` before `edit_newsletter.js`');

    ServerComm.toast = function(message, duration = 2000)
    {
        Materialize.toast(message, duration)
    };

    // TODO: We could provide a progress bar using ServerComm.useProgressBar($progressBar)

    ServerComm.loginModal.$loginModal = $('#login-modal');

    $('#save-btn').click(function()
    {
        ServerComm.doSave({
            /*TODO: nid: newsletterId,*/
            newTitle: $('#ntitle').val(),
            newText: $('#markdown').val()
        })
    });

    // TODO: Register other action buttons here

});
