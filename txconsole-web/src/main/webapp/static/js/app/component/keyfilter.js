define(['jquery'], function ($) {

    function filterKey(field) {
        alert(field.input.val())
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