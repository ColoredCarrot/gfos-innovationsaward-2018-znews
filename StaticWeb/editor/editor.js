var quill;

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

    let BoldBlot = Quill.import('formats/bold');
    BoldBlot.tagName = 'B';   // Quill uses <strong> by default
    Quill.register(BoldBlot, true);

    quill = new Quill('#editor', {
        theme: 'snow',
        modules: {
            toolbar: toolbarStructure,
            syntax: true,
            formula: true,
            clipboard: true
        }
    });

    $('div.ql-editor').attr('tabindex', '2');

});

function setEditorContents(delta)
{
    quill.setContents(delta, 'silent');  // 'silent' source to disable undo
}
