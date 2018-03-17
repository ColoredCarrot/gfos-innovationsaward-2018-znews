jQuery(function($)
{

    Statistics.columns = ([{
        title: "Registrations",
        domId: "col-regs",
        entries: [
            {
                title: "contact.coloredcarrot@gmail.com",
                attrs: {
                    "Emails": "12/13",
                    "Subscribed": "12. January 2018"
                }
            }
        ]
    }, {
        title: "Newsletters",
        domId: "col-nid",
        entries: [
            {
                title: "Test 2",
                attrs: {
                    "Views": "312"
                }
            },{
                title: "Test 3",
                attrs: {
                    "Views": "12"
                }
            }
        ]
    }]);

});

var Statistics = (function(s)
{

    let columns;

    Object.defineProperty(s, 'columns', {
        get: function()
        {
            return columns;
        },
        set: function(newColumns)
        {
            columns = newColumns;
            updateColumns();
        }
    });

    let updateColumns = s.updateColumns = function()
    {

        let ENTRY_TEMPLATE_VARIABLES = {
            '{{TITLE}}': column => column.title,
            '{{ATTRS}}': column => Object.keys(column.attrs).reduce((res, attrName) => res + `<div class="chip">${attrName}: ${column.attrs[attrName]}</div>`, '')
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

            let entryTemplate = '<li class="collection-item"><span style="margin-right: 10px">{{TITLE}}</span>{{ATTRS}}</li>';

            column.entries
                  .map(entry => Object.keys(ENTRY_TEMPLATE_VARIABLES)
                                      .map(key => [key, ENTRY_TEMPLATE_VARIABLES[key]])
                                      .map(e => [e[0], e[1](entry)])
                                      .reduce((res, e) => res.replace(e[0], e[1]), entryTemplate))
                  .map(html => $(html))
                  .forEach($e => $column.append($e));

            let $wrappedColumn = $(`<div class="col s${columnWidth}" id="${column.domId}"></div>`)
                .append($column);

            if (column.callbacks && column.callbacks.insert)
                column.callbacks.insert($column, column);

            $columnsContainer.append($wrappedColumn);

            if (column.callbacks && column.callbacks.inserted)
                column.callbacks.inserted($column, column);

        });

    };

    s.setColumns = function(columns = [])
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

    };


    return s;
})(Statistics || {
    columns: []
});
