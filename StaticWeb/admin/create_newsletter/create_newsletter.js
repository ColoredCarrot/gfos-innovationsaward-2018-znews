function displayGlobalToast(message, duration = 2000)
{
    Materialize.toast(message, duration);
}

function getNidIfKnown()
{
    return undefined;
}

function globalSwal(p1, p2, p3)
{
    return swal(p1, p2, p3);
}

var LoginModal = {
    get$: function()
    {
        return $('#login-modal');
    },
    open: function()
    {
        LoginModal.get$().modal('open');
    },
    close: function()
    {
        LoginModal.get$().modal('close');
    }
};

$(function()
{
    LoginModal.get$().modal();
});
