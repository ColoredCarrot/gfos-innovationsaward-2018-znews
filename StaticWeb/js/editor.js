(function($)
{

    $(document).ready(function() { Materialize.updateTextFields(); });

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

})(jQuery);