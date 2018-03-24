var DeltaRenderer = (function(dr)
{

    dr.renderToDOM = function(delta, container, displayMode)  // display mode, not inline mode? Then, add .flow-text
    {

        let BoldBlot = Quill.import('formats/bold');
        BoldBlot.tagName = 'B';   // Quill uses <strong> by default
        Quill.register(BoldBlot, true);

        let quill = new Quill(container, {
            modules: dr.options.loadModules,
            readOnly: true
        });

        quill.setContents(delta);

        $(container).find('blockquote').attr('style', 'margin: 20px 0; padding-left: 1.5rem');

        if (displayMode)
            $(container).find('*').filter((idx, e) => $(e).is('p') || $(e).is('pre')).addClass('flow-text');

        return container;

    };

    return dr;
})(DeltaRenderer || {
    options: {
        loadModules: {
            syntax: true,
            formula: true
        }
    }
});
