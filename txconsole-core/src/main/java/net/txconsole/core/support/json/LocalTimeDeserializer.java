package net.txconsole.core.support.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class LocalTimeDeserializer extends StdDeserializer<LocalTime> {

	public LocalTimeDeserializer() {
		super(LocalTime.class);
	}
	
	@Override
	public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return LocalTime.parse(jp.getText(), ISODateTimeFormat.localTimeParser());
	}

}
