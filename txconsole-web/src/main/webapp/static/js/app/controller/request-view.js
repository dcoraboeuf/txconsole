define(['jquery', 'render', 'ajax', 'jquery.typing', 'component/request'], function ($, render, ajax) {

    Handlebars.registerHelper('requestHideLanguage', function (locale) {
        return 'request.hide.language'.loc(locale)
    });

    function saveEntryForLocale(input, entryId, locale) {
        var newValue = $(input).val();
        var previousValue = $(input).attr('data-previous-value');
        if (newValue != previousValue) {
            ajax.put({
                url: 'ui/request/entry/{0}'.format(entryId),
                data: {
                    locale: locale,
                    value: newValue
                },
                loading: {
                    el: input
                },
                successFn: function (controlledEntryValue) {
                    // Change submitted
                    input.removeClass('warning').addClass('success');
                    // Updates the previous value
                    input.attr('data-previous-value', newValue);
                    // Updates the controls
                    var message = controlledEntryValue.messages[locale];
                    if (message) {
                        input
                            .addClass('translation-diff-input-invalid')
                            .attr('title', message);
                    } else {
                        input
                            .removeClass('translation-diff-input-invalid')
                            .removeAttr('title');
                    }
                }
            })
        }
    }

    function loadEntry(header, entryId, viewResource) {
        var editable = viewResource.actions.indexOf('PROJECT#REQUEST_EDIT') >= 0 && viewResource.data.summary.status == 'EXPORTED';
        ajax.get({
            url: 'ui/request/entry/{0}'.format(entryId),
            loading: {
                el: header
            },
            successFn: function (controlledEntry) {
                var editableEntry = editable && controlledEntry.diffEntry.type != 'DELETED';
                var container = $('#translation-key-content-{0}'.format(entryId));
                $.each(controlledEntry.diffEntry.entries, function (index, entryValue) {
                    // Adapt editable status according to the ACL
                    entryValue.editableAllowed = editableEntry;
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
                            // Shift+Enter for validation
                            $(input).keydown(function (e) {
                                // On Shit+Enter ==> submit
                                if (e.which == 13 && e.shiftKey) {
                                    // Sends the changes
                                    saveEntryForLocale($(input), entryId, locale);
                                    // Does not send the normal event
                                    e.preventDefault();
                                }
                            });
                            // Monitor the typing
                            $(input).typing({
                                start: function () {
                                    $(input).addClass('warning').removeClass('success');
                                }
                            });
                            // Validating when exiting the field
                            $(input).blur(function () {
                                // Sends the changes
                                saveEntryForLocale($(input), entryId, locale);
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
                                // Sends the changes
                                saveEntryForLocale(input, entryId, locale);
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
                                // Restores the previous value
                                input.val(input.attr('data-previous-value'));
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

    var localeFilter = {};

    function filterLocales(filter) {
        localeFilter = $.extend(localeFilter, filter);
        $('.locale-entry').each(function (index, tr) {
            var locale = $(tr).attr('data-locale');
            var hidden = localeFilter[locale];
            if (hidden) {
                $(tr).hide();
            } else {
                $(tr).show();
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
                    entry.invalid = true;
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
            });
            // Filter on deleted entries
            $('#request-hide-deleted').click(function () {
                if ($(this).hasClass('active')) {
                    $('.translation-entry-type-DELETED').show();
                } else {
                    $('.translation-entry-type-DELETED').hide();
                }
            });
            // Filter on invalid entries
            $('#request-hide-valid').click(function () {
                if ($(this).hasClass('active')) {
                    // if filter on deleted already active, avoid to display them.
                    if ($('#request-hide-deleted').hasClass('active')) {
                        $('#request-view').find('tr:not(.translation-entry-invalid)').not('.translation-entry-type-DELETED').show();
                    } else {
                        $('#request-view').find('tr:not(.translation-entry-invalid)').show();
                    }
                } else {
                    $('#request-view').find('tr:not(.translation-entry-invalid)').hide();
                }
            });
            // Filter on locales
            $('.request-hide-locale').each(function (index, btn) {
                var locale = $(btn).attr('data-locale');
                $(btn).click(function () {
                    var hidden = !$(btn).hasClass('active');
                    var filter = {};
                    filter[locale] = hidden;
                    filterLocales(filter);
                })
            });
        })
    }
})