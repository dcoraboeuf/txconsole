define(['jquery','render','jcombo','jconfigurable'], function ($, render, jcombo, jconfigurable) {

    function display (field) {
        render.renderInto(
            field.configContainer,
            'configuration/txsource/simple',
            {},
            function () {
                field.txFileSource = jcombo.init(field.configContainer.find('#txsource-simple-txfilesource'), {
                    url: 'ui/ref/txfilesource',
                    extension: jconfigurable.jcomboExtension({
                        path: 'txfilesource'
                    })
                });
                field.txFileFormat = jcombo.init(field.configContainer.find('#txsource-simple-txfileformat'), {
                    url: 'ui/ref/txfileformat',
                    extension: jconfigurable.jcomboExtension({
                        path: 'txfileformat'
                    })
                });
            }
        )
    }

    function val (value, field) {
        value.node = {};
        value.node.txFileSourceConfigured = field.txFileSource.val();
        value.node.txFileFormatConfigured = field.txFileFormat.val();
    }

    return {
        display: display,
        val: val
    }

});