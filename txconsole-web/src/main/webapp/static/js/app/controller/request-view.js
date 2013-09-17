define(['jquery', 'render', 'component/request'], function ($, render) {
    return {
        url: function (config) {
            return 'ui/request/{0}/view'.format(config.requestId)
        },
        preProcessingFn: function (config, resource) {
            $.each(resource.data.diff.entries, function (index, entry) {
                switch (entry.type) {
                    case 'ADDED':
                        entry.cls = 'success';
                        break;
                    case 'UPDATED':
                        entry.cls = 'info';
                        break;
                    case 'DELETED':
                        entry.cls = 'error';
                        break;
                }
            });
            return resource;
        },
        render: render.asSimpleTemplate('request-view')
    }
})