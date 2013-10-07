define(['jquery', 'ajax', 'render', 'handlebars'], function ($, ajax, render) {

    var projectId = $('#project').val();
    var branchId = $('#branch').val();

    function prepareResults() {
        // Hiding locales
        $('.contribution-locale-hide').each(function (i, action) {
            var locale = $(action).attr('data-locale');
            $(action).click(function () {
                $('.locale-{0}'.format(locale)).hide();
            })
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