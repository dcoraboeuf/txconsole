define(['render'], function (render) {

    return {
        url: function (config) {
            if (config.entity) {
                return 'ui/events?entity={0}&entityId={1}'.format(config.entity, config.entityId)
            } else {
                return 'ui/events?u=1'
            }
        },
        render: render.asTableTemplate(
            'events'
        )
    }

});