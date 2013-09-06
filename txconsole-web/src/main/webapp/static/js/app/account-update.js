define(['jquery', 'ajax', 'common'], function ($, ajax, common) {

    // Updating the account
    function accountUpdate() {
        var accountId = $('#accountId').val();
        ajax.put({
            url: 'ui/account/{0}'.format(accountId),
            data: common.values($('#account-form')),
            successFn: function () {
                'account'.goto();
            },
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#account-form-error')))
        });
        // Does not send
        return false;
    }

    $('#account-form').submit(accountUpdate);

});