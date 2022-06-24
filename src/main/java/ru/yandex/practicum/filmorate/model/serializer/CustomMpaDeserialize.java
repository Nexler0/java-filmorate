package ru.yandex.practicum.filmorate.model.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.io.IOException;

public class CustomMpaDeserialize extends StdDeserializer<Mpa> {

    public CustomMpaDeserialize(){
        this(null);
    }

    protected CustomMpaDeserialize(Class<?> vc) {
        super(vc);
    }

    @Override
    public Mpa deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode jsonNode =  objectCodec.readTree(jsonParser);
        int id = jsonNode.get("id").asInt();
        return new Mpa(id);
    }

}
