jQuery(function($)
{

    Statistics.init();

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

    let init = s.init = function()
    {
        $.ajax('/admin/api/statistics', {
            cache: false,
            dataType: 'json'
        })
         .then(function success(data)
         {
             console.log(data);
             let { registrations, publications } = data;

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

             let makeEditButton = function(email)
             {
                 return {
                     innerMarkup: '<i class="material-icons">mode_edit</i>',
                     clickHandler: function handleClick(entry, $entry, column)
                     {
                         if (entry.title !== email)
                             throw new Error(`entry.title (${entry.title}) does not match email (${email})`);
                         displayAndHandleEditRegPrompt(email)
                             .then(data =>
                             {
                                 console.assert(data.success);

                                 // Update UI
                                 entry.title = data.newemail;

                                 // Replace old with new email in $entry
                                 $entry.find(':contains("' + data.oldemail + '")').text((idx, oldText) => oldText.replace(data.oldemail, data.newemail));
                                 $entry.attr('data-title', data.newemail);

                                 swal.stopLoading();
                                 swal.close();

                                 Materialize.toast("Email address updated", 4000);  // TODO: Add undo button

                             }, error =>
                             {
                                 if (error === 'cancellation')
                                     return;
                                 window.console.error(error);
                                 switch (error.status)
                                 {
                                     case 403:
                                         CommonSwals.notLoggedIn({ forceLogin: true, loginTarget: '/admin/statistics' });
                                         break;
                                     case 404:
                                         swal("Error", "You have specified an invalid email address.", 'error', {
                                             buttons: [true, "Retry"]
                                         })
                                             .then(value =>
                                             {
                                                 if (value)
                                                 // Retry
                                                     entry.buttons[0].clickHandler(entry, $entry, column);
                                             });
                                         break;
                                     default:
                                         CommonSwals.internalError();
                                         break;
                                 }
                             });
                     }
                 };
             };

             function formatDate(date)
             {
                 const monthNames = [
                     "January", "February", "March",
                     "April", "May", "June", "July",
                     "August", "September", "October",
                     "November", "December"
                 ];

                 let day = date.getDate(),
                     monthIndex = date.getMonth(),
                     year = date.getFullYear();

                 return `${monthNames[monthIndex]} ${day}, ${year}`;
             }

             // At the moment, registrations is just an array of emails
             let regColEntries = Object.keys(registrations).map(email =>
                 ({
                     title: email,
                     attrs: {
                         "Registered": formatDate(new Date(registrations[email].dateRegistered))
                     },
                     buttons: [
                         makeEditButton(email)
                     ]
                 }));
             let registrationsColumn = {
                 title: "Registrations",
                 domId: "col-regs",
                 entries: regColEntries
             };

             let pubColEntries = publications.map(pub =>
                 ({
                     title: pub.title,
                     nid: pub.nid,
                     attrs: {
                         "Views": pub.views,
                         "ID": pub.nid
                     },
                     buttons: [
                         {
                             innerMarkup: '<i class="material-icons">pageview</i>',
                             clickHandler: function(entry, $entry, column)
                             {
                                 if (entry.title !== pub.title)
                                     throw new Error(`entry.title (${entry.title}) does not match pub.title (${pub.title})`);
                                 console.assert(entry.nid === pub.nid);
                                 window.location.href = '/view?nid=' + pub.nid;
                             }
                         },
                         {
                             innerMarkup: '<i class="material-icons">mode_edit</i>',
                             clickHandler: function(entry, $entry, column)
                             {
                                 if (entry.title !== pub.title)
                                     throw new Error(`entry.title (${entry.title}) does not match pub.title (${pub.title})`);
                                 console.assert(entry.nid === pub.nid);
                                 window.location.href = '/admin/edit_newsletter?nid=' + pub.nid;
                             }
                         }
                     ]
                 }));
             let publicationsColumn = {
                 title: "Publications",
                 domId: "col-pubs",
                 entries: pubColEntries
             };

             s.columns = ([registrationsColumn, publicationsColumn]);

         }, function error(error)
         {
             console.error(error);

             switch (error.status)
             {
                 case 403:
                     CommonSwals.notLoggedIn({ forceLogin: true, loginTarget: '/admin/statistics' });
                     break;
                 default:
                     CommonSwals.internalError();
                     break;
             }
         });
    };

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

            let entryTemplate = '<li data-title="{{TITLE}}"><div class="collapsible-header">{{TITLE}}</div><div class="collapsible-body">{{BODY}}</div></li>';

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
                                       .reduce((res, e) => res.split(e[0]).join(e[1]), entryTemplate));
                      let $eBody = $e.find('div.collapsible-body');
                      if (entry.buttons)
                          entry.buttons
                               .map(btn =>
                               {
                                   let $btn = $(`<a class="btn-floating" style="margin-right: 10px">${btn.innerMarkup}</a>`);
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

    return s;
})(Statistics || {
    columns: []
});
