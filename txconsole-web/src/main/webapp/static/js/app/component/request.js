define(['dialog', 'ajax', 'application'], function (dialog, ajax, application) {

    /**
     * Creating a request for a branch
     */
    function createRequest(branchId) {
        ajax.get({
            url: 'ui/branch/{0}/request'.format(branchId),
            successFn: function (requestConfigurationData) {
                dialog.show({
                    title: 'request.create'.loc(),
                    templateId: 'request-create',
                    data: requestConfigurationData
                })
            }
        })
    }

    return {
        createRequest: createRequest
    }

});