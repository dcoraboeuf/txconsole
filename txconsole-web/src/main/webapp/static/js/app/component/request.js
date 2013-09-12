define(
    ['dialog', 'ajax', 'application', 'jcombo', 'jconfigurable', 'component/keyfilter', 'render'],
    function (dialog, ajax, application, jcombo, jconfigurable, keyfilter, render) {

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
                        width: 800,
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
                            // FIXME Key filter - disabled temporarily (diff priority)
                            /**
                             keyFilterField = keyfilter.init(dialog.get('#request-key-filter'), {
                                project: requestConfigurationData.data.project.id,
                                branch: requestConfigurationData.data.branch.id,
                                help: 'request.create.keys.filter.help'.loc(),
                                resultsFn: function (map) {
                                    render.renderInto(
                                        dialog.get('#request-key-results'),
                                        'request-keyfilter-results',
                                        map
                                    )
                                }
                            });
                             */
                        },
                        submitFn: function (dialog) {
                            // Exchange data
                            var txFileExchangeConfig = txExchangeJCombo.val();
                            // Sends the request
                            ajax.post({
                                url: 'ui/branch/{0}/request'.format(branchId),
                                data: {
                                    version: dialog.get('#request-version').val(),
                                    txFileExchangeConfig: txFileExchangeConfig
                                    // FIXME Additional keys
                                },
                                successFn: function () {
                                    dialog.closeFn();
                                    // Goes back to the branch
                                    application.goLink(requestConfigurationData, 'branch-gui');
                                }
                            })
                        }
                    })
                }
            })
        }

        return {
            createRequest: createRequest
        }

    }
);