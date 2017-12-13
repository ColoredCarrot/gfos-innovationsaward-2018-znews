jQuery(function($)
{
    if (!ServerComm)
        throw new Error('ServerComm not defined! Please include `server-comm.js` before `edit_newsletter.js`');

    ServerComm.toast = function(message, duration = 2000)
    {
        Materialize.toast(message, duration)
    };

    // Change tab focus order. Must be done after iframe has loaded
    let $editorFrame = $('#editor-frame');
    $editorFrame.on('load', () => $editorFrame.contents().find('#markdown').attr('tabindex', '2'));

    // TODO: We could provide a progress bar using ServerComm.useProgressBar($progressBar)

    ServerComm.loginModal.$loginModal = $('#login-modal');

    $('#save-btn').click(function()
    {
        ServerComm.doSave({
            /*TODO: nid: newsletterId,*/
            newTitle: $('#ntitle').val(),
            newText: $editorFrame.contents().find('#markdown').val()
        })
    });

    // TODO: Register other action buttons here

});
