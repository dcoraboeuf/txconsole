define(['render'], function (render) {

    function display (configContainer) {
        render.renderInto(
            configContainer,
            'extension/configuration/txfileformat/extension-txfilesource-properties'
        )
    }

    return {
        display: display
    }

});