define(['jquery', 'component/request'], function ($, request) {

    var projectId = $('#project').val();
    var branchId = $('#branch').val();

    $('#request-create').click(function () {
        request.createRequest(branchId)
    });

});