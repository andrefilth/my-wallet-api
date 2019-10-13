package com.amedigital.wallet.endoint.response.serializer;

import com.amedigital.wallet.exceptions.AmeException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ExceptionSerializer extends StdSerializer<AmeException> {

    public ExceptionSerializer() {
        this(null);
    }

    private ExceptionSerializer(Class<AmeException> t) {
        super(t);
    }

    @Override
    public void serialize(AmeException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("error", value.getErrorCode());

        if (value.getMessage() != null) {
            gen.writeStringField("error_description", value.getMessage());
        }

        if (value.getFields() != null && !value.getFields().isEmpty()) {
            gen.writeObjectField("fields", value.getFields());
        }
    }

}
