define(['render'], function (render) {

    function display (configContainer) {
        render.renderInto(
            configContainer,
            'extension/configuration/txfilesource/extension-txfilesource-svn'
        )
    }

    return {
        display: display
    }

});