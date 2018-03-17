jQuery(function($)
{

    function editRegMakeRequest(oldEmail, email)
    {
        EMAIL = email;
        return $.ajax('/admin/api/admin_edit_registration', {
            data: { oldemail: oldEmail, newemail: email },
            dataType: 'json',
            method: 'post'
        });
    }

    /**
     * Quick and dirty email validation;
     *  not to be relied on
     */
    function validateEmail(email)
    {
        return email.length >= 3 && /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(email);
    }

    function displayEditRegPrompt(oldEmail, invalidEmail = false)
    {
        return swal({
            title: "Edit registration",
            content: $(`<input id="edit-reg-input" type="email" placeholder="${oldEmail}" autofocus="autofocus" class="validate${invalidEmail ? " invalid" : ""}">`).on('keyup', function(evt)
            {
                if (evt.keyCode === 13)
                {
                    evt.stopPropagation();
                    evt.preventDefault();
                    $('.swal-button--confirm').click();  // This cannot be the best way
                }
            })[0],
            buttons: {
                cancel: true,
                confirm: {
                    text: "Confirm",
                    closeModal: false
                }
            },
            closeOnClickOutside: false,
            closeOnEsc: false
        });
    }

    function displayAndHandleEditRegPrompt(oldEmail, a = displayEditRegPrompt(oldEmail))
    {
        return a.then(email =>
        {
            if (email === null)
            {
                // Cancel
                return Promise.reject('cancellation');
            }
            email = $('#edit-reg-input').val();
            if (!validateEmail(email))
                return displayAndHandleEditRegPrompt(oldEmail, displayEditRegPrompt(oldEmail, true));
            return editRegMakeRequest(oldEmail, email);
        });
    }

    Statistics.columns = ([{
        title: "Registrations",
        domId: "col-regs",
        entries: [
            {
                title: "contact.coloredcarrot@gmail.com",
                attrs: {
                    "Emails": "12/13",
                    "Subscribed": "12. January 2018"
                },
                buttons: [
                    {
                        innerMarkup: '<i class="material-icons">mode_edit</i>',
                        clickHandler: function(entry, $entry, column)
                        {
                            displayAndHandleEditRegPrompt(entry.title)
                                .then(data =>
                                {
                                    console.log(data);
                                }, error =>
                                {
                                    if (error === 'cancellation')
                                        return;
                                    window.console.error(error);
                                    swal("Error", "You have specified an invalid email address.", 'error', {
                                        buttons: [true, "Retry"]
                                    })
                                        .then(value =>
                                        {
                                            if (value)
                                                // Retry
                                                entry.buttons[0].clickHandler(entry, $entry, column);
                                        });
                                });
                        }
                    }
                ]/*,
                preInsertAttrs: function(column)
                {
                    return `<a class="btn-floating" title="Edit"><i class="material-icons">edit</i></a>`;
                }*/
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
            }, {
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
            '{{BODY}}': column =>
            {
                let r;
                //if (column.preInsertAttrs)
                //    r = column.preInsertAttrs(column);
                r = Object.keys(column.attrs).reduce((res, attrName) => res + `<div class="chip">${attrName}: ${column.attrs[attrName]}</div>`, '');
                //if (column.postInsertAttrs)
                //    r = column.postInsertAttrs() || r;
                return r;
            }
        };

        if (columns.length > 4)
            throw new Error(`number of columns (${columns.length}) exceeds maximum (4)`);

        let $columnsContainer = $('#general-stats-container');
        let numColumns = columns.length;
        let columnWidth = 12 / numColumns;

        $('.collapsible').collapsible('destroy');
        $columnsContainer.empty();

        columns.forEach((column, idx) =>
        {

            let $column = $('<ul class="collapsible" data-collapsible="expandable"></ul>');

            $column.append($(`<li><h5 style="margin-left: 10px">${column.title}</h5></li>`));

            let entryTemplate = '<li><div class="collapsible-header">{{TITLE}}</div><div class="collapsible-body">{{BODY}}</div></li>';

            column.entries
                  /*.map(entry => Object.keys(ENTRY_TEMPLATE_VARIABLES)
                                      .map(key => [key, ENTRY_TEMPLATE_VARIABLES[key]])
                                      .map(e => [e[0], e[1](entry)])
                                      .reduce((res, e) => res.replace(e[0], e[1]), entryTemplate))
                  .map(html => $(html))*/
                  .map(entry =>
                  {
                      let $e = $(Object.keys(ENTRY_TEMPLATE_VARIABLES)
                                       .map(key => [key, ENTRY_TEMPLATE_VARIABLES[key]])
                                       .map(e => [e[0], e[1](entry)])
                                       .reduce((res, e) => res.replace(e[0], e[1]), entryTemplate));
                      let $eBody = $e.find('div.collapsible-body');
                      if (entry.buttons)
                          entry.buttons
                               .map(btn =>
                               {
                                    let $btn = $(`<a class="btn-floating">${btn.innerMarkup}</a>`);
                                    if (btn.clickHandler)
                                        $btn.on('click', function()
                                        {
                                            btn.clickHandler(entry, $e, column);
                                        });
                                    return $btn;
                               })
                               .reverse()
                               .forEach($btn =>
                               {
                                   $eBody.prepend($btn);
                               });
                      return $e;
                  })
                  .forEach($e => $column.append($e));

            let $wrappedColumn = $(`<div class="col s${columnWidth}" id="${column.domId}"></div>`)
                .append($column);

            if (column.callbacks && column.callbacks.insert)
                column.callbacks.insert($column, column);

            $columnsContainer.append($wrappedColumn);

            if (column.callbacks && column.callbacks.inserted)
                column.callbacks.inserted($column, column);

        });

        $('.collapsible').collapsible();

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
