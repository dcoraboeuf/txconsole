define(
    ['dialog', 'ajax', 'application', 'jcombo', 'jconfigurable', 'common', 'component/keyfilter', 'render'],
    function (dialog, ajax, application, jcombo, jconfigurable, common) {

        Handlebars.registerHelper('requestStatus', function (status, message) {
            var statusName = 'request.status.{0}'.format(status).loc();
            var title;
            if (message && message.code) {
                title = message.code.loc(message.parameters).html();
            } else {
                title = statusName;
            }
            // FIXME #9 Title as tooltip
            var html = '';
            html += '<img src="{0}"/> '.format(application.staticPathTo('images/request-status-{0}.png'.format(status)));
            html += statusName;
            return html;
        });

        /**
         * Creating a request for a branch
         */
        function createRequest(branchId) {
            var txExchangeJCombo;
            var keyFilterField;
            ajax.get({
                url: 'ui/branch/{0}/request/config'.format(branchId),
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
                                successFn: function (request) {
                                    dialog.closeFn();
                                    // Goes to the request page
                                    application.gui(request);
                                }
                            })
                        }
                    })
                }
            })
        }

        function deleteRequest(requestId) {
            common.confirmAndCall(
                'request.delete.prompt'.loc(),
                function () {
                    ajax.del({
                        url: 'ui/request/{0}'.format(requestId),
                        successFn: function (branch) {
                            // Goes to the branch
                            application.gui(branch)
                        }
                    })
                }
            )
        }

        return {
            createRequest: createRequest,
            deleteRequest: deleteRequest
        }

    }
);