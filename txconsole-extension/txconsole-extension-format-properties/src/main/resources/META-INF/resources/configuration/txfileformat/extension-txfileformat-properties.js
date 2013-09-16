define(['jquery', 'render'], function ($, render) {


    return {
        display: function (field) {
            render.renderInto(
                field.configContainer,
                'extension/configuration/txfileformat/extension-txfilesource-properties'
            )
        },
        val: function (value, field) {
            var groups = [];
            var text = field.configContainer.find('#txfileformat-properties-groups').val();
            var lines = text.split('\n');
            $.each(lines, function (no, line) {
                line = line.trim();
                if (line != '' && line.charAt(0) != '#') {
                    var eqPos = line.indexOf('=');
                    if (eqPos > 0) {
                        var group = {
                            name: line.substring(0, eqPos).trim(),
                            locales: []
                        };
                        var locales = line.substring(eqPos + 1).trim().split(',');
                        $.each(locales, function (i, locale) {
                            group.locales.push(locale.trim());
                        });
                        groups.push(group);
                    }
                }
            });
            value.node = {
                defaultLocale: field.configContainer.find('#txfileformat-properties-defaultLocale').val(),
                groups: groups
            };
        }
    }

});