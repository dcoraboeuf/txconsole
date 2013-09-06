package net.txconsole.core.support.json;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.joda.time.LocalDateTime;

import java.io.IOException;

public class LocalDateTimeSerializer extends SerializerBase<LocalDateTime> {

	public LocalDateTimeSerializer() {
		super(LocalDateTime.class);
	}

	@Override
	public void serialize(LocalDateTime value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		jgen.writeString(value.toString("yyyy-MM-dd'T'HH:mm"));
	}

}
