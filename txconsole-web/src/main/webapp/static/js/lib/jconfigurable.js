define(['jquery', 'require'], function ($, require) {

    function jcomboExtension(extensionConfig) {
        var configContainer;
        return {
            init: function (container, jcomboConfig) {
                // Adds the container for the configuration form
                configContainer = $('<div></div>')
                    .addClass('hidden')
                    .appendTo(container);
            },
            onChange: function (container, id, jcomboConfig) {
                // No selection?
                if (id == '') {
                    configContainer.hide();
                    configContainer.empty();
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
                        configContainer.empty();
                        controller.display(configContainer);
                        configContainer.show();
                    });
                }
            }
        }
    }

    return {
        jcomboExtension: jcomboExtension
    }

});