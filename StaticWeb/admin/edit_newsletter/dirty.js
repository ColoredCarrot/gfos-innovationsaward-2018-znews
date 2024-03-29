/*
Display warning onBeforeUnload
if form has been edited
 */

var dirty_hash = {

    allowDisplayWarning: false,

    hashString: function(str)
    {
        if (str.length === 0)
            return 0;
        let hash = 0,
            i,
            chr;
        for (i = 0; i < str.length; i++)
        {
            chr = str.charCodeAt(i);
            hash = ((hash << 5) - hash) + chr;
            hash |= 0; // Convert to 32bit integer
        }
        return hash;
    },

    hash: '00',

    recompute: function()
    {
        dirty_hash.hash = dirty_hash.compute();
    },

    compute: function()
    {
        let title = $('#ntitle').val(),
            text = JSON.stringify($('#editor-frame')[0].contentWindow.quill.getContents());
        return '' + dirty_hash.hashString(title) + dirty_hash.hashString(text);
    },

    onbeforeunloadHandler: function()
    {
        if (dirty_hash.allowDisplayWarning && dirty_hash.hash !== dirty_hash.compute())
            return "You have unsaved changes!\nAre you sure you want to leave and discard them?";
    }

};

jQuery(function($)
{
    window.onbeforeunload = dirty_hash.onbeforeunloadHandler;
});
