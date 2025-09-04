package co.com.pragma.crediya.api.helper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public final class JsonErrorMessageFactory {

    private JsonErrorMessageFactory() {}

    public static boolean isJsonDecodeError(Throwable ex) {
        return ex instanceof org.springframework.web.server.ServerWebInputException
                || ex instanceof org.springframework.core.codec.DecodingException
                || ex instanceof JsonParseException
                || ex instanceof JsonMappingException;
    }

    public static String build(Throwable ex) {
        if (ex instanceof JsonParseException jpe) {
            var loc = jpe.getLocation();
            if (loc != null && loc.getLineNr() > 0) {
                return String.format("Cuerpo JSON malformado cerca de la línea %d, columna %d. Revise comas, comillas y valores.",
                        loc.getLineNr(), loc.getColumnNr());
            }
            return "Cuerpo JSON malformado. Revise comas, comillas y valores.";
        }
        if (ex instanceof MismatchedInputException mie) {
            String field = mie.getPath() != null && !mie.getPath().isEmpty()
                    ? mie.getPath().get(0).getFieldName()
                    : null;
            if (field != null && !field.isBlank()) {
                return "Tipo inválido para el campo '" + field + "'. Verifique el formato del valor.";
            }
            return "Tipos de datos inválidos en el cuerpo JSON.";
        }
        return "Cuerpo JSON malformado o tipos inválidos. Verifique comas, comillas y valores.";
    }
}
