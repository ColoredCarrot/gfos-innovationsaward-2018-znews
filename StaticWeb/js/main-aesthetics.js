/*
Core aesthetics functionality.
Adjusts div.background's height css dynamically
(correctly placing the footer),
makes the main ZNEWS image (img[src="/img/logo.jpg"]) clickable,
and adds the footer.
 */

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
        $('body').append($(`<footer class="page-footer">
    <div class="container">
        <div class="row">
            <div class="col l6 s12">
                <h5 class="white-text">ZNews Project</h5>
                <p class="grey-text text-lighten-4">
                    The ZNews Project was created by Julian Bennet Koch
                    and Mats Holtbecker to be entered into the GFOS
                    Innovationsaward 2018 contest. It, entailing any
                    related components that are not owned by another party,
                    including (but not limited to) the source code and/or
                    binary files of the entire website and the server,
                    is not to be used by a third party, profit or non-profit,
                    nor be published, distributed, or in any way made available
                    to anyone for any reason without express, written permission
                    from one of the authors.
                </p>
            </div>
            <div class="col l4 offset-l2 s12">
                <h5 class="white-text">Navigation</h5>
                <ul>
                    <li>
                        <a class="grey-text text-lighten-3" href="/">Main Page</a>
                        <i class="material-icons left nav-link-bullet">keyboard_arrow_right</i>
                    </li>
                    <li>
                        <a class="grey-text text-lighten-3" href="/admin/dashboard">Administrator Login</a>
                        <i class="material-icons left nav-link-bullet">keyboard_arrow_right</i>
                    </li>
                    <li>
                        <a class="grey-text text-lighten-3" href="/admin/logout">Logout</a>
                        <i class="material-icons left nav-link-bullet nav-link-bullet-indent">keyboard_arrow_right</i>
                    </li>
                    <li>
                        <a class="grey-text text-lighten-3" href="/admin/change_password">Change Password</a>
                        <i class="material-icons left nav-link-bullet">keyboard_arrow_right</i>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            © 2018 Julian Koch, Mats Holtbecker ALL RIGHTS RESERVED
        </div>
    </div>
</footer>
`));
    }
});
