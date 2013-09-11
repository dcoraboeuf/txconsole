package net.txconsole.core.support.json;

import net.txconsole.core.model.TranslationMap;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

public class TranslationMapSerializer extends JsonSerializer<TranslationMap> {

    private final ObjectMapper mapper;

    public TranslationMapSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void serialize(TranslationMap map, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeTree(mapper.valueToTree(map.toFlatMap()));
    }
}
