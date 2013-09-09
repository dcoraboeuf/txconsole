define(['jquery', 'require'], function ($, require) {

    function jcomboExtension(extensionConfig) {
        return {
            init: function (field) {
                // Adds the container for the configuration form
                field.configContainer = $('<div></div>')
                    .addClass('hidden')
                    .appendTo(field.container);
            },
            onChange: function (field, id) {
                // No selection?
                if (id == '') {
                    field.configContainer.hide();
                    field.configContainer.empty();
                } else {
                    // Gets the full path to the configuration controller
                    var path;
                    if (id.indexOf('extension-') == 0) {
                        path = 'extension/configuration/{0}/{1}'.format(extensionConfig.path, id);
                    } else {
                        path = 'configuration/{0}/{1}'.format(extensionConfig.path, id);
                    }
                    // Loads the configuration controller asynchronously
                    require([path], function (controller) {
                        // On load, init the configuration box
                        field.configContainer.empty();
                        controller.display(field.configContainer);
                        field.configContainer.show();
                    });
                }
            }
        }
    }

    return {
        jcomboExtension: jcomboExtension
    }

});