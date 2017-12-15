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

        // Get newsletter ID
        let nid = $('#-data-nid-container').attr('data-nid');
        nid = typeof nid !== typeof undefined && nid !== false ? nid : null;

        let saveData = {
            newTitle: $('#ntitle').val(),
            newText: $editorFrame.contents().find('#markdown').val()
        };

        if (nid)
        {
            saveData.nid = nid;
            saveData.nidConsumer = function(assignedNid)
            {
                if (assignedNid !== nid)
                {
                    swal('Internal Error', 'Returned assigned newsletter ID does not match stored ID', 'error')
                        .then(() => { throw new Error('INTERNAL ERROR: Returned assigned newsletter ID does not match stored ID') });
                }
            }
        }
        else
            saveData.nidConsumer = function(assignedNid)
            {
                $('#-data-nid-container').attr('data-nid', assignedNid);
            };

        ServerComm.doSave(saveData);

    });

    // TODO: Register other action buttons here

});
