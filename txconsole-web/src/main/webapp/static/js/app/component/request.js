define(
    ['jquery', 'dialog', 'ajax', 'application', 'jcombo', 'jconfigurable', 'common', 'component/keyfilter', 'render'],
    function ($, dialog, ajax, application, jcombo, jconfigurable, common) {

        Handlebars.registerHelper('requestStatus', function (status, message) {
            var statusName = 'request.status.{0}'.format(status).loc();
            var title;
            var status = $('<i></i>')
                .append(
                    $('<img/>').attr('src', application.staticPathTo('images/request-status-{0}.png'.format(status)))
                )
                .append('&nbsp;')
                .append($('<b></b>').text(statusName));
            if (message && message.code) {
                title = message.code.loc(message.parameters);
                status.append(
                    $('<div></div>').text(title).addClass('alert alert-error')
                );
            }
            return status.html();
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

        /**
         * Uploads a response for the request
         */
        function uploadRequest(requestId) {
            var index = 1;
            dialog.show({
                title: 'request.upload'.loc(),
                width: 800,
                templateId: 'request-upload',
                buttons:[{
                    text: 'general.submit'.loc(),
                    action: 'upload',
                    cls: 'btn-primary',
                    click: function (dialog) {
                        dialog.form.submit();
                    }
                }, {
                    text: 'general.cancel'.loc(),
                    action: 'cancel'
                }],
                data: {
                    requestId: requestId
                },
                initFn: function (dialog) {
                    dialog.get('#request-upload-file-add').click(function () {
                        dialog.get('#request-upload-files')
                            .append(
                                $('<div></div>')
                                    .append(
                                        $('<input/>')
                                            .attr('type', 'file')
                                            .attr('name', 'response-{0}'.format(index++))
                                            .attr('required', 'required')
                                    )
                            )
                    });
                }
            })
        }

        /**
         * Merges the request
         */
        function mergeRequest(requestId) {
            // Gets the last view status
            ajax.get({
                url: 'ui/request/{0}/view'.format(requestId),
                successFn: function (resource) {
                    // Confirmation for forcing the merge if there are still invalid entries
                    var force = resource.data.invalid;
                    // Dialog to enter the merge parameters
                    dialog.show({
                        title: 'request.merge'.loc(),
                        templateId: 'request-merge',
                        data: {
                            force: force
                        },
                        submitFn: function (dialog) {
                            dialog.closeFn();
                            ajax.post({
                                url: 'ui/request/{0}/merge'.format(requestId),
                                data: {
                                    force: force,
                                    message: dialog.get('#request-merge-message').val()
                                },
                                successFn: function (resource) {
                                    // Reloading the request page
                                    application.gui(resource);
                                }
                            })
                        }
                    });
                }
            })
        }

        return {
            createRequest: createRequest,
            deleteRequest: deleteRequest,
            uploadRequest: uploadRequest,
            mergeRequest: mergeRequest
        }

    }
);