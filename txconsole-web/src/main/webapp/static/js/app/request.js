define(['jquery', 'component/request'], function ($, request) {

    var projectId = $('#project').val();
    var branchId = $('#branch').val();
    var requestId = $('#request').val();

    $('#request-upload').click(function () {
        request.uploadRequest(requestId)
    });
    $('#request-delete').click(function () {
        request.deleteRequest(requestId)
    });

});