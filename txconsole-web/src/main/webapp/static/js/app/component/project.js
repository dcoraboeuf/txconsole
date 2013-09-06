define(['dialog','ajax','application'], function(dialog, ajax, application) {

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
                        application.gui(project);
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    }

    return {
        createProject: createProject
    }
})