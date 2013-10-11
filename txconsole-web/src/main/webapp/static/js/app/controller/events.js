define(['render'], function (render) {

    return {
        url: function (config) {
            if (config.entity) {
                return 'ui/events?{0}={1}'.format(config.entity, config.entityId)
            } else {
                return 'ui/events'
            }
        },
        render: render.asTableTemplate(
            'events'
        )
    }

});