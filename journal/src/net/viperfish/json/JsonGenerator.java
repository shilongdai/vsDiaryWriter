package net.viperfish.json;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonGenerator {
	protected ObjectMapper objectMapper;

	public JsonGenerator() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.setDateFormat(DateFormat.getTimeInstance(DateFormat.FULL));
	}

	public String toJson(Object o) throws JsonGenerationException, JsonMappingException {
		StringWriter w = new StringWriter();
		try {
			objectMapper.writeValue(w, o);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return w.toString();

	}

	public <T> T fromJson(Class<T> srcClass, String data) throws JsonParseException, JsonMappingException {
		T result = null;
		try {
			result = objectMapper.readValue(data, srcClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
