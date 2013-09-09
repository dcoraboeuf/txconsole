define(['jquery', 'ajax'], function ($, ajax) {

    function init(container, config) {
        // DOM
        var select = $('<select></select>').attr('required', 'required').appendTo(container);
        var descriptionEl = $('<div></div>')
            .addClass('hidden')
            .addClass('description')
            .appendTo(container);
        // Handle on the field
        var field = {
            container: container,
            config: config,
            select: select,
            descriptionEl: descriptionEl
        };
        // Additional elements?
        if (config.extension && config.extension.init) {
            config.extension.init(field);
        }
        // Filling the <select> element
        ajax.get({
            url: config.url,
            successFn: function (sources) {
                // Empty default element
                select.empty();
                select.append($('<option></option>').attr('value', '').text('-'));
                // Index
                var sourceIndex = {};
                // All elements
                $.each(sources, function (index, source) {
                    $('<option></option>')
                        .attr('value', source.id)
                        .text(source.name)
                        .appendTo(select);
                    sourceIndex[source.id] = source;
                });
                // On change
                select.change(function () {
                    // Description
                    var id = select.val();
                    if (id != '') {
                        descriptionEl.text(sourceIndex[id].description);
                        descriptionEl.show();
                    } else {
                        descriptionEl.text('');
                        descriptionEl.hide();
                    }
                    // Additional selection?
                    if (config.extension && config.extension.onChange) {
                        config.extension.onChange(field, id)
                    }
                });
            }
        });
        // val() function - returns the selection and its configuration
        field.val = function () {
            var id = field.select.val();
            if (id != '') {
                var value = {
                    id: id
                };
                // Extension
                if (config.extension && config.extension.val) {
                    config.extension.val(value, field);
                }
                // OK
                return value;
            } else {
                // No selection, nothing to return
            }
        }
        // OK, returns the handle for future reference
        return field;
    }

    return {
        init: init
    }

});