/**
 * An extension for the standard server-comm.js module
 */
var ServerComm = (function(sc)
{

    sc.doGetArticles = function(callback, amount = 5)
    {

        let $ = sc.$,
            swal = sc.swal;

        let updateProgress = function(percentComplete)
        {
            if (sc.onUpdateProgress)
                sc.onUpdateProgress(percentComplete);
        };



    };

}(ServerComm || {
    onUpdateProgress: null,
    swal: swal,
    $: jQuery
}));
