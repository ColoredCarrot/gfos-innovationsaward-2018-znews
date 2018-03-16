jQuery(function($)
{

    setColumns([{
        title: "Registrations",
        domId: "col-regs",
        entries: [
            {
                title: "contact.coloredcarrot@gmail.com",
                attrs: {
                    "Successfully sent emails": "12/13"
                }
            }
        ]
    }]);

});

function setColumns(columns = [])
{

    /*
    column.
        domId: string
        title: string
        entries: [
            entry.
                title: string
                attrs: { attrName1: attrVal1, attrName2: attrVal2 }
        ]
        callbacks.
            insert: function($column, column)    // pre  column insertion
            inserted: function($column, column)  // post column insertion

     */

    let ENTRY_TEMPLATE_VARIABLES = {
        '{{TITLE}}': column => column.title,
        '{{ATTRS}}': column => Object.keys(column.attrs).reduce((res, attrName) => res + `<span>${attrName}: ${column.attrs[attrName]}</span>`, '')
    };

    if (columns.length > 4)
        throw new Error(`number of columns (${columns.length}) exceeds maximum (4)`);

    let $columnsContainer = $('#general-stats-container');
    let numColumns = columns.length;
    let columnWidth = 12 / numColumns;

    $columnsContainer.empty();

    columns.forEach((column, idx) =>
    {

        let $column = $('<ul class="collection with-header"></ul>');

        $column.append($(`<li class="collection-header"><h5>${column.title}</h5></li>`));

        let entryTemplate = '<li class="collection-item"><span class="title">{{TITLE}}</span><p>{{ATTRS}}</p></li>';

        column.entries
              .map(entry => Object.keys(ENTRY_TEMPLATE_VARIABLES)
                                  .map(key => [key, ENTRY_TEMPLATE_VARIABLES[key]])
                                  .map(e => [e[0], e[1](entry)])
                                  .reduce((res, e) => res.replace(e[0], e[1]), entryTemplate))
              .map(html => $(html))
              .forEach($e => $column.append($e));

        let $wrappedColumn = $(`<div class="col s${(idx + 1) * columnWidth}" id="${column.domId}"></div>`)
            .append($column);

        if (column.callbacks && column.callbacks.insert)
            column.callbacks.insert($column, column);

        $columnsContainer.append($wrappedColumn);

        if (column.callbacks && column.callbacks.inserted)
            column.callbacks.inserted($column, column);

    });

}
