define(['jquery', 'component/request'], function ($, request) {

    var projectId = $('#project').val();
    var branchId = $('#branch').val();
    var requestId = $('#request').val();

    $('#request-delete').click(function () {
        request.deleteRequest(requestId)
    });

});