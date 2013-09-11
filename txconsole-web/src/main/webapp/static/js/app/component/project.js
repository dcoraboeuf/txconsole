define(['dialog', 'ajax', 'application', 'jcombo', 'jconfigurable', 'common'], function (dialog, ajax, application, jcombo, jconfigurable, common) {

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
                console.log(txSourceConfig);
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

    function deleteProject (id) {
        ajax.get({
            url: 'ui/project/{0}'.format(id),
            successFn: function (resource) {
                common.confirmAndCall(
                    'project.delete.prompt'.loc(resource.data.name),
                    function () {
                        ajax.del({
                            url: 'ui/project/{0}'.format(id),
                            successFn: function (home) {
                                application.gui(home)
                            }
                        })
                    }
                )
            }
        })
    }

    return {
        createProject: createProject,
        deleteProject: deleteProject
    }
});