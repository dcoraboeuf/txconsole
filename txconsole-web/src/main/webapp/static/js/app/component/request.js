define(['dialog', 'ajax', 'application', 'jcombo', 'jconfigurable'], function (dialog, ajax, application, jcombo, jconfigurable) {

    /**
     * Creating a request for a branch
     */
    function createRequest(branchId) {
        var txExchangeJCombo;
        ajax.get({
            url: 'ui/branch/{0}/request'.format(branchId),
            successFn: function (requestConfigurationData) {
                dialog.show({
                    title: 'request.create'.loc(),
                    templateId: 'request-create',
                    data: requestConfigurationData,
                    initFn: function (dialog) {
                        txExchangeJCombo = jcombo.init(dialog.get('#request-txfileexchange'), {
                            url: 'ui/ref/txfileexchange',
                            extension: jconfigurable.jcomboExtension({
                                path: 'txfileexchange'
                            })
                        })
                    }
                })
            }
        })
    }

    return {
        createRequest: createRequest
    }

});