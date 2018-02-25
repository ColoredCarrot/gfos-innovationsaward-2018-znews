jQuery(function($)
{

    let toolbarStructure = [
        [ { 'size': [ 'small', 'normal', 'large', 'huge' ] } ],
        [ 'bold', 'italic', 'underline', 'strike' ],
        [ { 'header': 1 }, { 'header': 2 }, 'blockquote', 'code-block', { 'list': 'ordered' }, { 'list': 'bullet' } ],
        [ 'link', 'image', 'video', 'formula' ],
        [ { 'indent': '-1' }, { 'indent': '+1' }, { 'align': [] } ],
        [ 'clean' ]
    ];

    let quill = new Quill('#editor', {
        theme: 'snow',
        modules: {
            toolbar: toolbarStructure,
            syntax: true,
            formula: true,
            clipboard: true
        }
    });

});
