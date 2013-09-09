define(['jquery'], function ($) {

    function display (configContainer) {
        // TODO TxFileSource & TxFileFormat
        $('<span></span>').text('Test').appendTo(configContainer)
    }

    return {
        display: display
    }

});