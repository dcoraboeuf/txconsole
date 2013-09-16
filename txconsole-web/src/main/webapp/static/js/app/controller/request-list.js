define(['render', 'component/request'], function (render) {

    return {
        url: function (config) {
            return 'ui/branch/{0}/request?u=1'.format(config.branchId)
        },
        render: render.asTableTemplate('request-list')
    }
})