define(['jquery', 'render', 'ajax', 'component/request'], function ($, render, ajax) {

    function loadEntry(header, entryId, viewResource) {
        ajax.get({
            url: 'ui/request/entry/{0}'.format(entryId),
            loading: {
                el: header
            },
            successFn: function (entry) {
                var container = $('#translation-key-content-{0}'.format(entryId));
                // Adapt editable status according to the ACL
                $.each(entry.entries, function (index, diff) {
                    diff.editableAllowed = diff.editable && viewResource.actions.indexOf('PROJECT#REQUEST_EDIT') >= 0;
                });
                // Rendering
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
        render: render.asSimpleTemplate('request-view', render.sameDataFn, function (config, resource) {
            // Activates each key to gets its content
            $('.translation-key-load').each(function (index, e) {
                $(e).click(function () {
                    loadEntry($(e), $(e).attr('data-request-entry-id'), resource)
                })
            })
        })
    }
})