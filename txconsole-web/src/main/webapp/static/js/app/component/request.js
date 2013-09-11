define(['dialog', 'ajax', 'application', 'jcombo', 'jconfigurable', 'component/keyfilter'], function (dialog, ajax, application, jcombo, jconfigurable, keyfilter) {

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
                        // Selection of the file exchange
                        txExcFIXMEhangeJCombo = jcombo.init(dialog.get('#request-txfileexchange'), {
                            url: 'ui/ref/txfileexchange',
                            extension: jconfigurable.jcomboExtension({
                                path: 'txfileexchange'
                            })
                        });
                        // Key filter on RETURN
                        dialog.get('#request-key-filter').keydown(function (e) {
                            if (e.which == 13) {
                                keyfilter.filterKey({
                                    filter: dialog.get('#request-key-filter').val()
                                });
                                e.preventDefault();
                            }
                        });
                        // Key filter on the Search button
                        dialog.get('#request-key-filter-go').click(function () {
                            keyfilter.filterKey({
                                filter: dialog.get('#request-key-filter').val()
                            });
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