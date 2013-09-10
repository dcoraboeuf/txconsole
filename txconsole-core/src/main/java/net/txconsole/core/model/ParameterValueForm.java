package net.txconsole.core.model;

import com.google.common.base.Function;
import lombok.Data;

@Data
public class ParameterValueForm {

    public static final Function<? super ParameterValueForm, String> nameFn = new Function<ParameterValueForm, String>() {
        @Override
        public String apply(ParameterValueForm p) {
            return p.getName();
        }
    };
    private final String name;
    private final String value;

}
