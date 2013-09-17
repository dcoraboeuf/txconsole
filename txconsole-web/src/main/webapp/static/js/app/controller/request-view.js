define(['jquery', 'render', 'ajax', 'component/request'], function ($, render, ajax) {

    function loadEntry(header, entryId) {
        ajax.get({
            url: 'ui/request/entry/{0}'.format(entryId),
            loading: {
                el: header
            },
            successFn: function (entry) {
                var container = $('#translation-key-content-{0}'.format(entryId));
                render.renderInto(
                    container,
                    'request-view-key',
                    {
                        entry: entry
                    },
                    function () {
                        $(header).hide();
                        $(container).removeClass('hidden');
                    }
                )
            }
        })
    }

    return {
        url: function (config) {
            return 'ui/request/{0}/view'.format(config.requestId)
        },
        preProcessingFn: function (config, resource) {
            $.each(resource.data.diff.entries, function (index, entry) {
                switch (entry.type) {
                    case 'ADDED':
                        entry.cls = 'success';
                        entry.icon = 'icon-plus';
                        break;
                    case 'UPDATED':
                        entry.cls = 'info';
                        entry.icon = 'icon-pencil';
                        break;
                    case 'DELETED':
                        entry.cls = 'error';
                        entry.icon = 'icon-minus';
                        break;
                }
            });
            return resource;
        },
        render: render.asSimpleTemplate('request-view', render.sameDataFn, function (config) {
            $('.translation-key-load').each(function (index, e) {
                $(e).click(function () {
                    loadEntry($(e), $(e).attr('data-request-entry-id'))
                })
            })
        })
    }
})