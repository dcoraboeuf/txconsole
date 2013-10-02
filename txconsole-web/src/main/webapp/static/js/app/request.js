define(['jquery', 'component/request'], function ($, request) {

    var projectId = $('#project').val();
    var branchId = $('#branch').val();
    var requestId = $('#request').val();

    $('#request-merge').click(function () {
        request.mergeRequest(requestId)
    });
    $('#request-upload').click(function () {
        request.uploadRequest(requestId)
    });
    $('#request-delete').click(function () {
        request.deleteRequest(requestId)
    });

    $('#request-hide-deleted').click(function() {
        if(!$(this).hasClass('active')) {
            $('tr.translation-entry-type-DELETED').hide();
        } else {
            $('tr.translation-entry-type-DELETED').show();
        }
    });

    $('#request-hide-valid').click(function() {
            if(!$(this).hasClass('active')) {
                $('tr:not(.translation-entry-invalid)').hide();
            } else {
                // if filter on deleted already active, avoid to display them.
                if($('#request-hide-deleted').hasClass('active')) {
                    $('tr:not(.translation-entry-invalid)').not('.translation-entry-type-DELETED').show();
                } else {
                    $('tr:not(.translation-entry-invalid)').show();
                }

            }
        });


});