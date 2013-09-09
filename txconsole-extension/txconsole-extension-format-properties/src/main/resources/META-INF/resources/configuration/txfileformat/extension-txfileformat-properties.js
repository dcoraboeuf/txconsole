define(['render', 'common'], function (render, common) {


    return {
        display: function (field) {
            render.renderInto(
                field.configContainer,
                'extension/configuration/txfileformat/extension-txfilesource-properties'
            )
        },
        val: function (value, field) {
            value.node = common.values(field.configContainer);
        }
    }

});