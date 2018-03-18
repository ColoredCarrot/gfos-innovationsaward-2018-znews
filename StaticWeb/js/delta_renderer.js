var DeltaRenderer = (function(dr)
{

    let virtContainer = $('<div class="ql-editor"/>').appendTo($('<div/>'))[0];  // Must wrap in two divs because of third-party bug (e.g. https://github.com/angular/angular.js/issues/13174)
    let quill = new Quill(virtContainer, {
        modules: dr.options.loadModules
    });

    dr.renderToHTML = function(delta)
    {
        quill.setContents(Array.isArray(delta) ? { ops: delta } : delta);
        let result = virtContainer.getElementsByClassName("ql-editor")[0].innerHTML;
        quill.setContents([{ insert: '\n' }]);  // cleanup
        return result;
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
