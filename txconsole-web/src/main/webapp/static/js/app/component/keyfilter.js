define(['jquery','ajax'], function ($, ajax) {

    function filterKey(field) {
        var filter = field.input.val().trim();
        if (filter != '') {
            ajax.post({
                url: 'ui/map/{0}'.format(field.config.branch),
                data: {
                    filter: filter
                },
                loading: {
                    el: field.go
                },
                successFn: function (map) {
                    console.log('map', map);
                }
            })
        }
    }

    function init(container, config) {
        // Field to create
        var field = {
            container: container,
            config: config
        };
        // Input field
        field.input = $('<input/>').attr('type', 'text').attr('size', 40).attr('maxlength', 80).appendTo(container);
        // Search button
        field.go = $('<button></button>').addClass('btn').attr('type', 'button').text('keyfilter.go'.loc()).appendTo(container);
        // Help?
        if (config.help) {
            $('<span></span>').addClass('help-block').text(config.help);
        }
        // Key filter on RETURN
        field.input.keydown(function (e) {
            if (e.which == 13) {
                filterKey(field);
                e.preventDefault();
            }
        });
        // Key filter on the Search button
        field.go.click(function () {
            filterKey(field);
        });
        // OK
        return field;
    }

    return {
        init: init
    }

});