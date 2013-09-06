package net.txconsole.core.validation;

import net.txconsole.core.InputException;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.MultiLocalizable;

import java.util.Arrays;

public class ValidationException extends InputException {

    public ValidationException(String code, Object... params) {
        this(new MultiLocalizable(
                Arrays.asList(
                        new LocalizableMessage(code, params)
                )
        ));
    }

    public ValidationException(MultiLocalizable multiLocalizable) {
        super(multiLocalizable);
    }

}
