define(['jquery','render','jcombo','jconfigurable'], function ($, render, jcombo, jconfigurable) {

    function display (configContainer) {
        render.renderInto(
            configContainer,
            'configuration/txsource/simple',
            {},
            function () {
                jcombo.init(configContainer.find('#txsource-simple-txfilesource'), {
                    url: 'ui/ref/txfilesource'
                });
                jcombo.init(configContainer.find('#txsource-simple-txfileformat'), {
                    url: 'ui/ref/txfileformat'
                });
            }
        )
    }

    return {
        display: display
    }

});