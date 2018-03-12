jQuery(function($)
{

    // TODO: Duplicate code
    function getQueryParamByName(name, url = window.location.href)
    {
        name = name.replace(/[\[\]]/g, "\\$&");
        const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
            results = regex.exec(url);
        if (!results)
            return null;
        if (!results[2])
            return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

    let nid = getQueryParamByName('nid');
    nid ? publication(nid) : overview();

    function overview()
    {
        $.ajax('/admin/statistics/email/overview', {
            cache: true,
            dataType: 'html'
        }).then(
            function success(data)
            {
                $('#stats-container').append(data);
            },
            function error(jqXHR)
            {
                console.error(jqXHR);
            });

    }

    function publication(nid)
    {

    }

});
