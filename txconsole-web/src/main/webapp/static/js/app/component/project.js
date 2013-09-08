define(['dialog', 'ajax', 'application'], function (dialog, ajax, application) {

    /**
     * Creating a project
     */
    function createProject() {
        dialog.show({
            title: 'project.create'.loc(),
            templateId: 'project-create',
            initFn: function (dialog) {
                // TODO Extracts this code to the 'application' library (or dedicated one maybe)
                // ... for the management of Descriptible or Configurable objects
                // DOM
                var container = dialog.get('#project-txsource');
                var select = $('<select></select>').attr('required', 'required').appendTo(container);
                var descriptionEl = $('<div></div>')
                    .addClass('hidden')
                    .addClass('description')
                    .appendTo(container);
                ajax.get({
                    url: 'ui/ref/txsource',
                    successFn: function (sources) {
                        // Empty default element
                        select.empty();
                        select.append($('<option></option>').text('-'));
                        // Index
                        var sourceIndex = {};
                        // All elements
                        $.each(sources, function (index, source) {
                            $('<option></option>')
                                .attr('value', source.id)
                                .text(source.name)
                                .appendTo(select);
                            sourceIndex[source.id] = source;
                        });
                        // On change
                        select.change(function () {
                            // TODO Callback handler
                            // Description
                            var id = select.val();
                            if (id != '') {
                                descriptionEl.text(sourceIndex[id].description);
                                descriptionEl.show();
                            } else {
                                descriptionEl.text('');
                                descriptionEl.hide();
                            }
                        });
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