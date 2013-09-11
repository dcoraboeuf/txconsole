define(['dialog', 'ajax', 'application', 'jcombo', 'jconfigurable', 'component/keyfilter'], function (dialog, ajax, application, jcombo, jconfigurable, keyfilter) {

    /**
     * Creating a request for a branch
     */
    function createRequest(branchId) {
        var txExchangeJCombo;
        var keyFilterField;
        ajax.get({
            url: 'ui/branch/{0}/request'.format(branchId),
            successFn: function (requestConfigurationData) {
                dialog.show({
                    title: 'request.create'.loc(),
                    templateId: 'request-create',
                    data: requestConfigurationData,
                    initFn: function (dialog) {
                        // Selection of the file exchange
                        txExchangeJCombo = jcombo.init(dialog.get('#request-txfileexchange'), {
                            url: 'ui/ref/txfileexchange',
                            extension: jconfigurable.jcomboExtension({
                                path: 'txfileexchange'
                            })
                        });
                        // Key filter
                        keyFilterField = keyfilter.init(dialog.get('#request-key-filter'), {
                            project: requestConfigurationData.data.project.id,
                            branch: requestConfigurationData.data.branch.id,
                            help: 'request.create.keys.filter.help'.loc()
                        });
                    }
                })
            }
        })
    }

    return {
        createRequest: createRequest
    }

});