jQuery(function($)
{



});

function setColumns(columns = [])
{

    /*
    column.
        title: string
        attrs: { attrName1: attrVal1, attrName2: attrVal2 }
        domId: string
        callbacks.
            insert: function($column, column)    // pre  column insertion
            inserted: function($column, column)  // post column insertion

     */

    let COLUMN_TEMPLATE_VARIABLES = {
        '{{TITLE}}': column => column.title,
        '{{ATTRS}}': column => Object.keys(column.attrs).reduce((res, attrName) => res + `<span>${attrName}: ${column.attrs[attrName]}</span>`, '')
    };

    if (columns.length > 4)
        throw new Error(`number of columns (${columns.length}) exceeds maximum (4)`);

    loadColumnTemplate().then(function success(data)
    {
        useColumnTemplate($(data));
    });

    function useColumnTemplate(columnTemplate)
    {
        let $columnsContainer = $('#general-stats-container');
        let numColumns = columns.length;
        let columnWidth = 12 / numColumns;

        $columnsContainer.empty();

        columns.forEach((column, idx) =>
        {

            let $column = $($.map(COLUMN_TEMPLATE_VARIABLES, (val, key) => [key, val])
                             .map(e => [e[0], e[1](column)])
                             .reduce((res, e) => res.replace(e[0], e[1]), columnTemplate));

            let $wrappedColumn = $(`<div class="col s${(idx + 1) * columnWidth}" id="${column.domId}"></div>`)
                .append($column);

            if (column.callbacks && column.callbacks.insert)
                column.callbacks.insert($column, column);

            $columnsContainer.append($wrappedColumn);

            if (column.callbacks && column.callbacks.inserted)
                column.callbacks.inserted($column, column);

        });
    }

    function loadColumnTemplate()
    {
        return $.ajax('/statistics/column.html', {
            dataType: 'text',
            cache: true
        });
    }

}
