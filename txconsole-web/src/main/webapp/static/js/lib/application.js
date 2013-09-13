define(['ajax', 'common'], function (ajax, common) {

    function deleteEntity(entityPath, id, callbackFn, nameFn) {
        var url = 'ui/manage/{0}/{1}'.format(entityPath, id);
        ajax.get({
            url: url,
            successFn: function (o) {
                var name;
                if (nameFn) {
                    name = nameFn(o);
                } else {
                    name = o.name;
                }
                common.confirmAndCall(
                    '{0}.delete.prompt'.format(extractEntity(entityPath)).loc(name),
                    function () {
                        ajax.del({
                            url: url,
                            successFn: function () {
                                callbackFn();
                            }
                        });
                    });
            }
        });
    }

    function extractEntity(value) {
        var pos = value.lastIndexOf('/');
        if (pos > 0) {
            return value.substring(pos + 1);
        } else {
            return value;
        }
    }

    function staticPathTo(relativePath) {
        // 'staticPath' variable is declared in 'layout.html'
        if (staticPath) {
            return '{0}/{1}'.format(staticPath, relativePath);
        } else {
            common.log('application')('Cannot find "staticPath" variable.');
            return 'static/{0}'.format(relativePath);
        }
    }

    function gui(resource) {
        goLink(resource, 'gui')
    }

    function goLink(resource, rel) {
        var href = link(resource, rel);
        if (href) {
            location.href = href;
        } else {
            common.log('application')('Cannot find link for {0}', rel);
        }
    }

    function link(resource, rel) {
        for (var i in resource.links) {
            var link = resource.links[i];
            if (link.rel == rel) {
                return link.href;
            }
        }
    }

    return {
        deleteEntity: deleteEntity,
        staticPathTo: staticPathTo,
        gui: gui,
        goLink: goLink,
        link: link
    }

});