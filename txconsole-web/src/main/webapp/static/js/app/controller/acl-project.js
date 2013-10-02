define(['render', 'jquery', 'ajax', 'dynamic'], function (render, $, ajax, dynamic) {

    return {
        url: function (config) {
            return 'ui/acl/project/{0}'.format(config.project);
        },
        render: render.asTableTemplate('acl-project', function (config) {
            $('.acl-delete').each(function (i, action) {
                var project = $(action).attr('data-project');
                var account = $(action).attr('data-account');
                $(action).click(function () {
                    ajax.del({
                        url: 'ui/acl/project/{0}/{1}'.format(project, account),
                        successFn: function () {
                            dynamic.reloadSection('acl-project');
                        }
                    })
                })
            });
        })
    }

});