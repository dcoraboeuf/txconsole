package net.txconsole.backend;

import com.google.common.base.Predicate;

public abstract class AbstractValidatorService {

    private final ValidatorService validatorService;

    public AbstractValidatorService(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    public <T> void validate(T value, Predicate<T> predicate, String code, Object... parameters) {
        validatorService.validate(value, predicate, code, parameters);
    }

    protected void validate(final Object o, Class<?> group) {
        validatorService.validate(o, group);
    }

}
