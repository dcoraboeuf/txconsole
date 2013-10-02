define(['render'], function (render) {

    return {
        url: 'ui/ref/project-role',
        render: render.asSimpleTemplate('project-roles', function (roles) {
            return {
                roles: roles
            }
        })
    }

});