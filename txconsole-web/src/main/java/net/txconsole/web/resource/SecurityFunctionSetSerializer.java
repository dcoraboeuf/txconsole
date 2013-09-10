package net.txconsole.web.resource;

import net.txconsole.core.security.SecurityFunction;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.util.Set;

public class SecurityFunctionSetSerializer extends JsonSerializer<Set<SecurityFunction>> {

    @Override
    public void serialize(Set<SecurityFunction> values, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        for (SecurityFunction fn : values) {
            jgen.writeString(fn.getCategory() + "#" + fn.name());
        }
        jgen.writeEndArray();
    }

}
