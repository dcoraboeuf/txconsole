define(['jquery', 'component/project'], function ($, project) {

    var projectId = $('#project').val();

    $('#project-delete').click(function () {
        project.deleteProject(projectId)
    });

});