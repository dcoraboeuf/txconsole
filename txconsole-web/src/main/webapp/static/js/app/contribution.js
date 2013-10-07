define(['jquery', 'ajax', 'render', 'handlebars', 'jquery.typing'], function ($, ajax, render) {

    var projectId = $('#project').val();
    var branchId = $('#branch').val();

    var contributions = [];

    function displayContributions() {
        render.renderInto(
            $('#manage-box'),
            'contribution-list',
            {
                contributions: contributions
            }
        )
    }

    function saveEdition(field) {
        // Gets the data
        var bundle = field.attr('data-bundle');
        var section = field.attr('data-section');
        var key = field.attr('data-key');
        var locale = field.attr('data-locale');
        var oldValue = field.attr('data-old-value');
        var value = field.val();
        if (value != oldValue) {
            // Sends the data to the contributions
            contributions.push({
                bundle: bundle,
                section: section,
                key: key,
                locale: locale,
                oldValue: oldValue,
                newValue: value
            });
            // Updates the contribution view
            displayContributions();
            // Clears the field status
            field.removeClass('contribution-field-ongoing');
        }
    }

    function prepareEditionField(field) {
        // Changes
        field.typing({
            start: function () {
                field.addClass('contribution-field-ongoing');
            }
        });
        // Special keys
        field.keydown(function (e) {
            // On Shit+Enter ==> submit
            if (e.which == 13 && e.shiftKey) {
                // Sends the changes
                saveEdition(field);
                // Does not send the normal event
                e.preventDefault();
            }
            // On escape, cancels the changes
            if (e.which == 27) {
                // Restores the old value
                field.val(field.attr('data-old-value'));
                // Removes the edition class
                field.removeClass('contribution-field-ongoing');
                // Does not send the normal event
                e.preventDefault();
            }
        });
        // Exiting the field
        field.blur(function (e) {
            saveEdition(field)
        })
    }

    function prepareResults() {
        // Hiding locales
        $('.contribution-locale-hide').each(function (i, action) {
            var locale = $(action).attr('data-locale');
            $(action).click(function () {
                $('.locale-{0}'.format(locale)).hide();
            })
        });
        // Edition
        $('.contribution-field').each(function (i, field) {
            prepareEditionField($(field));
        })
    }

    function displayResults(request, translationMapResponseResource) {
        render.withTemplate('contribution-field', function (contributionFieldTemplate) {

            Handlebars.registerHelper('contribution-field', function (entry, locale) {
                var label = entry.labels[locale];
                return contributionFieldTemplate({
                    key: entry.key,
                    locale: locale,
                    label: label
                })
            });

            render.renderInto(
                $('#edition-box'),
                'contribution-edition',
                {
                    request: request,
                    response: translationMapResponseResource
                },
                prepareResults
            )

        })
    }

    function search(token) {
        var request = {
            limit: 20,
            filter: token
        };
        ajax.post({
            url: 'ui/map/{0}'.format(branchId),
            data: request,
            loading: {
                el: $('#search-go')
            },
            successFn: function (translationMapResponseResource) {
                displayResults(request, translationMapResponseResource)
            }
        })
    }

    // Search form
    $('#search-form').submit(function () {
        // Search field
        var token = $('#search').val();
        if (token.trim() != '') {
            search(token.trim());
        }
        // No std submit
        return false;
    });

});