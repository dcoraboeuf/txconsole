define(['dialog', 'jquery', 'ajax'], function(dialog, $, ajax) {

    /**
     * Creating a project
     */
    function createProject() {
        dialog.show({
            title: 'project.create'.loc(),
            templateId: 'project-create',
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/project',
                    data: {
                        name: $('#project-name').val(),
                        description: $('#project-description').val()
                    },
                    successFn: function (project) {
                        config.closeFn();
                        'gui/project/{0}'.format(project.data.name.html()).goto();
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    }

    //

    $('#project-create').click(createProject);

});