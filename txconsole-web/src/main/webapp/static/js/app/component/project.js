define(['dialog','ajax','application'], function(dialog, ajax, application) {

    /**
     * Creating a project
     */
    function createProject() {
        dialog.show({
            title: 'project.create'.loc(),
            templateId: 'project-create',
            initFn: function (dialog) {
                // Loading of the sources
                ajax.get({
                    url: 'ui/ref/txsource',
                    successFn: function (sources) {
                        dialog.get('#project-txsource').empty();
                        $.each(sources, function (index, source) {
                            $('<option></option>')
                                .attr('value', source.id)
                                .text(source.nameKey.loc())
                                .appendTo(dialog.get('#project-txsource'));
                        })
                    }
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
})