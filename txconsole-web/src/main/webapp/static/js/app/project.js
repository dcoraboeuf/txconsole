define(['jquery', 'component/project', 'component/branch'], function ($, project, branch) {

    var projectId = $('#project').val();

    $('#project-delete').click(function () {
        project.deleteProject(projectId)
    });

    $('#branch-create').click(function () {
        branch.createBranch(projectId)
    });

});