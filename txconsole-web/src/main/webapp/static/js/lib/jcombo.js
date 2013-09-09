define(['jquery', 'ajax'], function ($, ajax) {

    function init(container, config) {
        // DOM
        var select = $('<select></select>').attr('required', 'required').appendTo(container);
        var descriptionEl = $('<div></div>')
            .addClass('hidden')
            .addClass('description')
            .appendTo(container);
        // Additional elements?
        if (config.extension && config.extension.init) {
            config.extension.init(container, config);
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
                        config.extension.onChange(container, id, config)
                    }
                });
            }
        })
    }

    return {
        init: init
    }

});