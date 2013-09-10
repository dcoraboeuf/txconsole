define(['render'], function (render) {
    return {
        url: function (config) {
            return 'ui/project/{0}/branch'.format(config.projectId)
        },
        render: render.asTableTemplate('branch-list')
    }
})