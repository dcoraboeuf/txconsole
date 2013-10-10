define(['dialog', 'ajax', 'application', 'common'], function (dialog, ajax, application, common) {

    /**
     * Creating a branch for a project
     */
    function createBranch(projectId) {
        // 1 - get the project information
        // 2 - get the project parameters
        $.when(
                ajax.get({url: 'ui/project/{0}'.format(projectId)}),
                ajax.get({url: 'ui/project/{0}/parameter'.format(projectId)})
            ).then(function (projectX, projectParameterX) {
                var project = projectX[0];
                var projectParameters = projectParameterX[0];
                dialog.show({
                    title: 'branch.create'.loc(),
                    width: 800,
                    templateId: 'branch-create',
                    data: {
                        project: project,
                        parameters: projectParameters
                    },
                    submitFn: function (dialog) {
                        var data = {
                            name: $('#branch-name').val(),
                            parameters: []
                        };
                        // Parameter values
                        dialog.get('.branch-parameter').each(function (index, input) {
                            var name = $(input).attr('data-parameter');
                            var value = $(input).val();
                            data.parameters.push({
                                name: name,
                                value: value
                            });
                        });
                        // Creation
                        ajax.post({
                            url: 'ui/project/{0}/branch'.format(projectId),
                            data: data,
                            successFn: function (branch) {
                                dialog.closeFn();
                                application.gui(branch);
                            },
                            errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                        });
                    }
                })
            });
    }

    function deleteBranch (id) {
        ajax.get({
            url: 'ui/branch/{0}'.format(id),
            successFn: function (resource) {
                common.confirmAndCall(
                    'branch.delete.prompt'.loc(resource.data.name),
                    function () {
                        ajax.del({
                            url: 'ui/branch/{0}'.format(id),
                            successFn: function (project) {
                                application.gui(project)
                            }
                        })
                    }
                )
            }
        })
    }

    return {
        createBranch: createBranch,
        deleteBranch: deleteBranch
    }

});