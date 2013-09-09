define(['jquery','render'], function ($, render) {

    function display (configContainer) {
        render.renderInto(
            configContainer,
            'configuration/txsource/simple',
            {},
            function () {

            }
        )
    }

    return {
        display: display
    }

});