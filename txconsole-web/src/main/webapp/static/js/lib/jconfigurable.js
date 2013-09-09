define(['jquery', 'require', 'common'], function ($, require, common) {

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
                        field.configController = controller;
                        // On load, init the configuration box
                        field.configContainer.empty();
                        controller.display(field.configContainer);
                        field.configContainer.show();
                    });
                }
            },
            val: function (value, field) {
                if (field.configController.val) {
                    field.configController.val(value, field.configContainer);
                } else {
                    throw 'Cannot find any val() function for configuration controller {0}'.format(field.container.attr('id'));
                }
            }
        }
    }

    return {
        jcomboExtension: jcomboExtension
    }

});