package mx.ucol.helpers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import mx.ucol.models.Todo;

public class JSON {
    public static Todo jsonToObject(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Todo todo = mapper.readValue(jsonString, Todo.class);

        return todo;
    }

    public static String objectToJson(Todo todo) throws JsonGenerationException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(todo);

        return json;
    }
}
