define(['render', 'component/request'], function (render, request) {

    return {
        url: function (config) {
            return 'ui/branch/{0}/request?u=1'.format(config.branchId)
        },
        render: render.asTableTemplate('request-list', function () {
            $('.request-delete').each(function (index, e) {
                $(e).click(function () {
                    request.deleteRequest($(e).attr('data-request-id'))
                })
            })
        })
    }
})