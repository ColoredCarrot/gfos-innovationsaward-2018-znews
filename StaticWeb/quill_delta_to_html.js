function renderQuillDeltaAsHTML(delta)
{

    let $containerDiv = $('<div><div id="virtual-quill-editor"></div></div>');

    let toolbarStructure = [
        [{ 'size': ['small', 'normal', 'large', 'huge'] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'header': 1 }, { 'header': 2 }, 'blockquote', 'code-block', { 'list': 'ordered' }, { 'list': 'bullet' }],
        ['link', 'image', 'video', 'formula'],
        [{ 'indent': '-1' }, { 'indent': '+1' }, { 'align': [] }],
        ['clean']
    ];

    let quill = new Quill($containerDiv.children('#virtual-quill-editor')[0], {
        theme: 'snow',
        modules: {
            toolbar: toolbarStructure,
            syntax: true,
            formula: true,
            clipboard: true
        }
    });

    quill.setContents(delta);

    return $containerDiv.find('div.ql-editor')[0].innerHTML;

}
