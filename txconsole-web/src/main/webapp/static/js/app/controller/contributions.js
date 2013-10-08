define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/branch/{0}/contribution'.format(config.branch)
        },
        render: render.asTableTemplate('contributions')
    }

});