package net.txconsole.web.support.fm;

import com.google.common.base.Functions;
import com.google.common.collect.Collections2;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;

public class FnLocLanguages implements TemplateMethodModel {

    private final Strings strings;

    public FnLocLanguages(Strings strings) {
        this.strings = strings;
    }

    @Override
    public Collection<String> exec(@SuppressWarnings("rawtypes") List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.isEmpty(), "List of arguments must be empty");
        // Gets the list of locales and returns them as strings
        return Collections2.transform(
                strings.getSupportedLocales().getSupportedLocales(),
                Functions.toStringFunction()
        );
    }

}
