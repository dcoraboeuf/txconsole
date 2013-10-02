define(['jquery', 'ajax', 'jquery-ui', 'bootstrap'], function ($, ajax) {

    var projectId = $('#project').val();

    function userLookup(query, processFn) {
        ajax.get({
            url: 'ui/account/lookup/{0}'.format(query)
        })
    }

    $('#acl-account').typeahead({
        source: userLookup
    });

});