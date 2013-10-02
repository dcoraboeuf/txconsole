define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/acl/project/{0}'.format(config.project);
        },
        render: render.asTableTemplate('acl-project')
    }

});