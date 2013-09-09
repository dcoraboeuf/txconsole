define(['jquery', 'ajax'], function ($, ajax) {

    function init(container, config) {
        // DOM
        var select = $('<select></select>').attr('required', 'required').appendTo(container);
        var descriptionEl = $('<div></div>')
            .addClass('hidden')
            .addClass('description')
            .appendTo(container);
        ajax.get({
            url: config.url,
            successFn: function (sources) {
                // Empty default element
                select.empty();
                select.append($('<option></option>').text('-'));
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
                    // TODO Callback handler
                    // Description
                    var id = select.val();
                    if (id != '') {
                        descriptionEl.text(sourceIndex[id].description);
                        descriptionEl.show();
                    } else {
                        descriptionEl.text('');
                        descriptionEl.hide();
                    }
                });
            }
        })
    }

    return {
        init: init
    }

});