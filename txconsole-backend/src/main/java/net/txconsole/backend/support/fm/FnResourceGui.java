package net.txconsole.backend.support.fm;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;
import net.txconsole.core.model.Resource;
import org.apache.commons.lang3.Validate;
import org.springframework.hateoas.Link;

import java.util.List;

public class FnResourceGui implements TemplateMethodModelEx {

    /**
     * @see net.txconsole.core.model.Resource
     */
    @Override
    public String exec(List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.size() == 1, "List of arguments must contain one element.");
        // Gets the first element as a Resource
        Resource<?> resource = (Resource<?>) DeepUnwrap.unwrap((TemplateModel) list.get(0));
        // Gets the GUI link from it...
        Link link = resource.getLink(Resource.REL_GUI);
        if (link != null) {
            return link.getHref();
        } else {
            // ... or fail
            throw new IllegalArgumentException("Cannot find any GUI link");
        }
    }

}
