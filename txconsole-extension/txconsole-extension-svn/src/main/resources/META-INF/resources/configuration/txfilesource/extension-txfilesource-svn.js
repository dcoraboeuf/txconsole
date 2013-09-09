define(['render','common'], function (render, common) {

    return {
        display: function (field) {
            render.renderInto(
                field.configContainer,
                'extension/configuration/txfilesource/extension-txfilesource-svn'
            )
        },
        val: function (value, field) {
            value.node = common.values(field.configContainer);
        }
    }

});