jQuery(function($)
{
    let onResize = () => $('div.background').css('height', window.innerHeight + 'px');
    $(window).resize(onResize);
    onResize();

    $('img[src="/img/logo.jpg"]').click(() => window.location.href = '/');

    // Add footer
    if (!$('main').length)
    {
        $('body').children().appendTo($('<main></main>').appendTo($('body')));
        $('body').append($('<footer class="page-footer">\n' +
            '    <div class="container">\n' +
            '        <div class="row">\n' +
            '            <div class="col l6 s12">\n' +
            '                <h5 class="white-text">ZNews Project</h5>\n' +
            '                <p class="grey-text text-lighten-4">\n' +
            '                    The ZNews Project was created by Julian Bennet Koch\n' +
            '                    and Mats Holtbecker to be entered into the GFOS\n' +
            '                    Innovationsaward 2018 contest. It, entailing any\n' +
            '                    related components that are not owned by another party,\n' +
            '                    including (but not limited to) the source code and/or\n' +
            '                    binary files of the entire website and the server,\n' +
            '                    is not to be used by a third party, profit or non-profit,\n' +
            '                    nor be published, distributed, or in any way made available\n' +
            '                    to anyone for any reason without express, written permission\n' +
            '                    from one of the authors.\n' +
            '                </p>\n' +
            '            </div>\n' +
            '            <div class="col l4 offset-l2 s12">\n' +
            '                <h5 class="white-text">Navigation</h5>\n' +
            '                <ul>\n' +
            '                    <li><a class="grey-text text-lighten-3" href="/">Main Page</a></li>\n' +
            '                    <li><a class="grey-text text-lighten-3" href="/admin/dashboard">Administrator Login</a></li>\n' +
            '                </ul>\n' +
            '            </div>\n' +
            '        </div>\n' +
            '    </div>\n' +
            '    <div class="footer-copyright">\n' +
            '        <div class="container">\n' +
            '            © 2018 Julian Koch, Mats Holtbecker ALL RIGHTS RESERVED\n' +
            '        </div>\n' +
            '    </div>\n' +
            '</footer>'));
    }
});
