define(['dialog', 'ajax', 'application', 'jcombo'], function (dialog, ajax, application, jcombo) {

    /**
     * Creating a project
     */
    function createProject() {
        dialog.show({
            title: 'project.create'.loc(),
            templateId: 'project-create',
            initFn: function (dialog) {
                jcombo.init(dialog.get('#project-txsource'), {
                    url: 'ui/ref/txsource'
                })
            },
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/project',
                    data: {
                        name: $('#project-name').val(),
                        fullName: $('#project-fullName').val()
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
});