define(['jquery', 'common', 'ajax', 'render', 'handlebars', 'jquery.typing'], function ($, common, ajax, render) {

    var branchId = $('#branch').val();
    var contributionId = $('#contribution').val();
    var direct = $('#direct').val() == 'true';
    var review = (contributionId > 0);
    var author = $('#author').val();
    var message = $('#message').val();

    var contributions = [];

    function indexOfContribution(id) {
        for (var i = 0; i < contributions.length; i++) {
            if (id == contributions[i].id) {
                return i;
            }
        }
        return -1;
    }

    function clearEditionBox() {
        $('#edition-box').empty().append(
            $('<div></div>').addClass('alert').addClass('alert-warning').text('contribution.edit.empty'.loc())
        )
    }

    function rejectContribution(btn) {
        common.confirmAndCall(
            'contribution.reject.prompt'.loc(),
            function () {
                ajax.del({
                    url: 'ui/contribution/{0}'.format(contributionId),
                    loading: {
                        el: $(btn)
                    },
                    successFn: function () {
                        'branch/{0}/contribution'.format(branchId).goto()
                    }
                })
            }
        )
    }

    function submitContributions() {
        if (contributions.length) {
            ajax.post({
                url: 'ui/branch/{0}/contribution'.format(branchId),
                data: {
                    contributions: $.map(contributions, function (contribution) {
                        return {
                            bundle: contribution.bundle,
                            section: contribution.section,
                            key: contribution.key,
                            locale: contribution.locale,
                            oldValue: contribution.oldValue,
                            newValue: contribution.newValue
                        }
                    }),
                    message: $('#submit-message').val(),
                    id: contributionId
                },
                loading: {
                    el: $('#submit-button')
                },
                successFn: function (result) {
                    // Clears the edition box
                    clearEditionBox();
                    // Clears the review
                    contributionId = 0;
                    review = false;
                    message = '';
                    // Clears the contributions
                    contributions = [];
                    displayContributions();
                    // Message
                    $('#submit-result-message').text(result.message);
                    $('#submit-result').show();
                }
            })
        }
    }

    function prepareContributionList() {
        // Removing a contribution
        $('.contribution-remove').each(function (i, action) {
            var id = $(action).attr('data-field-id');
            $(action).click(function () {
                // Restores the field old value (if present)
                $('#{0}'.format(id)).each(function (i, field) {
                    var oldValue = $(field).attr('data-old-value');
                    $(field).val(oldValue);
                    $(field).removeClass('contribution-field-ongoing').removeClass('contribution-field-edited');
                    // Removes the element from the contribution list
                    var index = indexOfContribution(id);
                    if (index >= 0) {
                        contributions.splice(index, 1);
                        displayContributions();
                    }
                })
            })
        });
        // Submit
        $('#submit-form').submit(function () {
            // Submit
            submitContributions();
            // No default submit
            return false;
        });
        // Rejecting a contribution
        $('#contribution-reject').click(function () {
            rejectContribution(this);
        });
    }

    function displayContributions() {
        render.renderInto(
            $('#manage-box'),
            'contribution-list',
            {
                contributions: contributions,
                direct: direct,
                review: review,
                message: review ? '{0} ({1})'.format(message, author) : ''
            },
            prepareContributionList
        )
    }

    function saveEdition(field) {
        // Gets the data
        var id = field.attr('id');
        var bundle = field.attr('data-bundle');
        var section = field.attr('data-section');
        var key = field.attr('data-key');
        var locale = field.attr('data-locale');
        var oldValue = field.attr('data-old-value');
        var value = field.val();
        if (value != oldValue) {
            var index = indexOfContribution(id);
            if (index < 0) {
                // Sends the data to the contributions
                contributions.push({
                    id: id,
                    bundle: bundle,
                    section: section,
                    key: key,
                    locale: locale,
                    oldValue: oldValue,
                    newValue: value
                });
            } else {
                // Edit the existing contribution
                contributions[index].newValue = value;
            }
            // Updates the contribution view
            displayContributions();
            // Clears the field status
            field.removeClass('contribution-field-ongoing').addClass('contribution-field-edited');
        }
    }

    function prepareEditionField(field) {
        // Changes
        field.typing({
            start: function () {
                field.addClass('contribution-field-ongoing').removeClass('contribution-field-edited');
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
                field.removeClass('contribution-field-ongoing').removeClass('contribution-field-edited');
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

    function getContributionId(entry, locale) {
        return '{0}-{1}-{2}-{3}'.format(entry.key.bundle, entry.key.section, entry.key.key, locale).replace('.', '_').replace('/', '_')
    }

    function displayResults(request, translationMapResponseResource) {
        render.withTemplate('contribution-field', function (contributionFieldTemplate) {

            Handlebars.registerHelper('contribution-field', function (entry, locale) {
                var label = entry.labels[locale];
                return contributionFieldTemplate({
                    id: getContributionId(entry, locale),
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

    // Loading for review
    if (review) {
        ajax.get({
            url: 'ui/contribution/{0}/details'.format(contributionId),
            successFn: function (details) {
                contributions = details;
                displayContributions();
            }
        })
    }

});