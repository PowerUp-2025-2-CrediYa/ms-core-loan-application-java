package co.com.pragma.crediya.api.helper;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.web.server.ServerWebInputException;

import static org.junit.jupiter.api.Assertions.*;

class JsonErrorMessageFactoryTest {

    @Test
    @DisplayName("should identify supported JSON decoding exceptions")
    void shouldIdentifyJsonDecodeErrors() {
        assertAll("Supported exceptions",
                () -> assertTrue(JsonErrorMessageFactory.isJsonDecodeError(new ServerWebInputException("Invalid input"))),
                () -> assertTrue(JsonErrorMessageFactory.isJsonDecodeError(new DecodingException("Decode error"))),
                () -> assertTrue(JsonErrorMessageFactory.isJsonDecodeError(new JsonParseException(null, "Malformed"))),
                () -> assertTrue(JsonErrorMessageFactory.isJsonDecodeError(new JsonMappingException(null, "Mapping error")))
        );
    }

    @Test
    @DisplayName("should build message for JsonParseException with location")
    void shouldBuildMessageForJsonParseExceptionWithLocation() {
        JsonLocation location = new JsonLocation("source", 100L, 1, 5);
        JsonParseException ex = new JsonParseException(null, "Malformed", location);

        String message = JsonErrorMessageFactory.build(ex);

        assertEquals("Cuerpo JSON malformado cerca de la línea 1, columna 5. Revise comas, comillas y valores.", message);
    }

    @Test
    @DisplayName("should build generic message for JsonParseException without location")
    void shouldBuildMessageForJsonParseExceptionWithoutLocation() {
        JsonParseException ex = new JsonParseException(null, "Malformed", (Throwable) null);

        String message = JsonErrorMessageFactory.build(ex);

        assertEquals("Cuerpo JSON malformado. Revise comas, comillas y valores.", message);
    }

    @Test
    @DisplayName("should build message for MismatchedInputException with field name")
    void shouldBuildMessageForMismatchedInputExceptionWithField() {
        MismatchedInputException ex = MismatchedInputException.from(null, String.class, "Invalid type");
        ex.prependPath(new JsonMappingException.Reference("fieldName"));

        String message = JsonErrorMessageFactory.build(ex);

        assertEquals("Tipos de datos inválidos en el cuerpo JSON.", message);
    }

    @Test
    @DisplayName("should build generic message for MismatchedInputException without field name")
    void shouldBuildMessageForMismatchedInputExceptionWithoutField() {
        MismatchedInputException ex = MismatchedInputException.from(null, String.class, "Invalid type");

        String message = JsonErrorMessageFactory.build(ex);

        assertEquals("Tipos de datos inválidos en el cuerpo JSON.", message);
    }

    @Test
    @DisplayName("should build fallback message for unknown exception")
    void shouldBuildFallbackMessageForUnknownException() {
        Throwable ex = new IllegalArgumentException("Unexpected");

        String message = JsonErrorMessageFactory.build(ex);

        assertEquals("Cuerpo JSON malformado o tipos inválidos. Verifique comas, comillas y valores.", message);
    }
}


