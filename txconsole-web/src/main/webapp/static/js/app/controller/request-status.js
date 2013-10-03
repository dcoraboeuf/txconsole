define(['jquery', 'render', 'component/request'], function ($, render) {
    return {
        url: function (config) {
            return 'ui/request/{0}'.format(config.requestId)
        },
        render: render.asSimpleTemplate('request-status', render.sameDataFn, function () {

            $('#request-hide-deleted').click(function() {
                if($(this).hasClass('active')) {
                    $('.translation-entry-type-DELETED').show();
                } else {
                    $('.translation-entry-type-DELETED').hide();
                }
            });

            $('#request-hide-valid').click(function() {
                if($(this).hasClass('active')) {
                    // if filter on deleted already active, avoid to display them.
                    if($('#request-hide-deleted').hasClass('active')) {
                        $('#request-view').find('tr:not(.translation-entry-invalid)').not('.translation-entry-type-DELETED').show();
                    } else {
                        $('#request-view').find('tr:not(.translation-entry-invalid)').show();
                    }
                } else {
                    $('#request-view').find('tr:not(.translation-entry-invalid)').hide();
                }
            });

        })
    }
})