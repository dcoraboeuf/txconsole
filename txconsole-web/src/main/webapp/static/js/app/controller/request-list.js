define(['render', 'application'], function (render, application) {

    Handlebars.registerHelper('requestStatus', function (status) {
        var html = '';
        html += '<img src="{0}"/> '.format(application.staticPathTo('images/request-status-{0}.png'.format(status)));
        html += 'request.status.{0}'.format(status).loc();
        return html;
    });

    return {
        url: function (config) {
            return 'ui/branch/{0}/request?u=1'.format(config.branchId)
        },
        render: render.asTableTemplate('request-list')
    }
})