define(['jquery', 'render', 'ajax', 'component/request'], function ($, render, ajax) {

    function saveEntryForLocale(input, entryId, locale, newValue, oldValue) {
        if (newValue != oldValue) {
            ajax.put({
                url: 'ui/request/entry/{0}'.format(entryId),
                data: {
                    locale: locale,
                    value: newValue
                },
                loading: {
                    el: input
                },
                successFn: function () {
                    input.removeClass('warning').addClass('success');
                }
            })
        }
    }

    function loadEntry(header, entryId, viewResource) {
        ajax.get({
            url: 'ui/request/entry/{0}'.format(entryId),
            loading: {
                el: header
            },
            successFn: function (controlledEntry) {
                var container = $('#translation-key-content-{0}'.format(entryId));
                $.each(controlledEntry.diffEntry.entries, function (index, entryValue) {
                    // Adapt editable status according to the ACL
                    entryValue.editableAllowed = entryValue.editable && viewResource.actions.indexOf('PROJECT#REQUEST_EDIT') >= 0;
                    // Collects the associated control if any
                    if (controlledEntry.diffControl) {
                        var message = controlledEntry.diffControl.messages[entryValue.locale];
                        if (message) {
                            entryValue.control = message;
                        }
                    }
                });
                // Rendering
                render.renderInto(
                    container,
                    'request-view-key',
                    {
                        entry: controlledEntry
                    },
                    function () {
                        $(header).hide();
                        $(container).removeClass('hidden');
                        // Inputs
                        $(container).find('.translation-edit-input').each(function (index, input) {
                            // Entry ID & locale
                            var entryId = $(input).attr('data-entry-id');
                            var locale = $(input).attr('data-locale');
                            // Key pressed
                            $(input).keydown(function (e) {
                                // On any key
                                $(input).addClass('warning');
                                // On Enter ==> submit
                                if (e.which == 13) {
                                    // Gets the new value & old value
                                    var newValue = $(input).val();
                                    var oldValue = $(input).attr('data-old-value');
                                    // Sends the changes
                                    saveEntryForLocale($(input), entryId, locale, newValue, oldValue);
                                }
                            });
                        });
                        // Submit buttons
                        $(container).find('.translation-edit-submit').each(function (index, btn) {
                            $(btn).click(function () {
                                // Entry ID & locale
                                var entryId = $(btn).attr('data-entry-id');
                                var locale = $(btn).attr('data-locale');
                                // Input
                                var input = $(container).find('#translation-edit-input-{0}-{1}'.format(entryId, locale));
                                // Gets the new value & old value
                                var newValue = input.val();
                                var oldValue = input.attr('data-old-value');
                                // Sends the changes
                                saveEntryForLocale(input, entryId, locale, newValue, oldValue);
                            })
                        });
                        // Cancel buttons
                        $(container).find('.translation-edit-cancel').each(function (index, btn) {
                            $(btn).click(function () {
                                // Entry ID & locale
                                var entryId = $(btn).attr('data-entry-id');
                                var locale = $(btn).attr('data-locale');
                                // Input
                                var input = $(container).find('#translation-edit-input-{0}-{1}'.format(entryId, locale));
                                // Restores the old value
                                input.val(input.attr('data-old-value'));
                                // Removes any class
                                if (input.hasClass('success')) {
                                    input.removeClass('success').addClass('warning');
                                } else {
                                    input.removeClass('warning');
                                }
                            })
                        });
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
                // Controls (may be null)
                entry.controls = resource.data.controls[entry.entryId];
                // CSS decoration
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
                // Mark for controls
                if (entry.controls) {
                    entry.cls += ' translation-diff-invalid';
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