define(['dialog', 'jquery', 'ajax'], function(dialog, $, ajax) {

    /**
     * Creating a pipeline
     */
    function createPipeline() {
        dialog.show({
            title: 'pipeline.create'.loc(),
            templateId: 'pipeline-create',
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/pipeline',
                    data: {
                        name: $('#pipeline-name').val(),
                        description: $('#pipeline-description').val()
                    },
                    successFn: function (pipeline) {
                        config.closeFn();
                        'gui/pipeline/{0}'.format(pipeline.data.name.html()).goto();
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    }

    //

    $('#pipeline-create').click(createPipeline);

});