define(['common'], function (common) {

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
        throw 'Cannot find link for "{0}"'.format(rel);
    }

    return {
        staticPathTo: staticPathTo,
        gui: gui,
        goLink: goLink,
        link: link
    }

});