define(['render', 'component/request'], function (render) {
    return {
        url: function (config) {
            return 'ui/request/{0}'.format(config.requestId)
        },
        render: render.asSimpleTemplate('request-status')
    }
})