define(['jquery', 'component/request', 'component/branch'], function ($, request, branch) {

    var projectId = $('#project').val();
    var branchId = $('#branch').val();

    $('#request-create').click(function () {
        request.createRequest(branchId)
    });
    $('#branch-delete').click(function () {
        branch.deleteBranch(branchId)
    });

});