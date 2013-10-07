define(['jquery', 'ajax'], function ($, ajax) {

    var projectId = $('#project').val();
    var branchId = $('#branch').val();

    function search(token) {
        ajax.post({
            url: 'ui/map/{0}'.format(branchId),
            data: {
                limit: 20,
                filter: token
            },
            loading: {
                el: $('#search-go')
            }
        })
    }

    // Search form
    $('#search-form').submit(function () {
        // Search field
        var token = $('#search').val();
        if (token.trim() != '') {
            search(token.trim());
        }
        // No std submit
        return false;
    });

});