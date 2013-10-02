define(['jquery', 'ajax', 'jquery-ui', 'bootstrap'], function ($, ajax) {

    var projectId = $('#project').val();

    var accountLabels;
    var accountsByNames;
    var selectedAccount;

    function userLookup(query, processFn) {
        ajax.get({
            url: 'ui/account/lookup/{0}'.format(query),
            successFn: function (accountSummaryResources) {
                accountLabels = [];
                accountsByNames = {};
                $.each(accountSummaryResources, function (i, accountSummaryResource) {
                    var accountLabel = accountSummaryResource.data.name + ' - ' + accountSummaryResource.data.fullName;
                    accountsByNames[ accountLabel] = accountSummaryResource;
                    accountLabels.push(accountLabel);
                });
                processFn(accountLabels);
            }
        })
    }

    $('#acl-account').typeahead({
        source: userLookup,
        matcher: function () {
            return true;
        },
        sorter: function (items) {
            return items;
        },
        highlighter: function (item) {
            var regex = new RegExp('(' + this.query + ')', 'gi');
            return item.replace(regex, "<strong>$1</strong>");
        },
        updater: function (accountLabel) {
            selectedAccount = accountsByNames[accountLabel];
            return accountLabel;
        }
    });

});