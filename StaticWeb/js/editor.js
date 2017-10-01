$(function()
{
    function onFormat()
    {
        const $this = $(this);
        $this.hasClass('active') ? formatDisable($this) : formatEnable($this)
    }

    function formatEnable($this)
    {
        $this.addClass('active');
    }

    function formatDisable($this)
    {
        $this.removeClass('active');
    }

    $('.format-icon').click(onFormat);

    //$('textarea.richtext').ckeditor();
    AlloyEditor.editable('richtext');

    $('body').click(function()
    {
        // on click
        // delegate to iframe's globalOnClick() function
        document.getElementById("toolbar-frame").contentWindow.globalOnClick();
    });

});
