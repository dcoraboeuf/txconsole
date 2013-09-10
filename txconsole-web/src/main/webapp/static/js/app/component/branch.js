define(['dialog', 'ajax', 'application', 'jcombo', 'jconfigurable', 'common'], function (dialog, ajax, application, jcombo, jconfigurable, common) {

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
                    }
                })
            });
    }

    return {
        createBranch: createBranch
    }

});