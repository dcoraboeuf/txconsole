define(['dialog', 'ajax', 'application', 'jcombo', 'jconfigurable'], function (dialog, ajax, application, jcombo, jconfigurable) {

    /**
     * Creating a project
     */
    function createProject() {
        var txSourceJCombo;
        dialog.show({
            title: 'project.create'.loc(),
            width: 800,
            templateId: 'project-create',
            initFn: function (dialog) {
                txSourceJCombo = jcombo.init(dialog.get('#project-txsource'), {
                    url: 'ui/ref/txsource',
                    extension: jconfigurable.jcomboExtension({
                        path: 'txsource'
                    })
                })
            },
            submitFn: function (config) {
                // Tx Source configuration
                var txSourceConfig = txSourceJCombo.val();
                // Sending the project creation
                ajax.post({
                    url: 'ui/project',
                    data: {
                        name: $('#project-name').val(),
                        fullName: $('#project-fullName').val(),
                        txSourceConfig: txSourceConfig
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