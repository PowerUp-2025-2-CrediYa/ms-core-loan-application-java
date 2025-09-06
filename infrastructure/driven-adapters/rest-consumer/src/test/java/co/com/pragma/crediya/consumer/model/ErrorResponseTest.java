package co.com.pragma.crediya.consumer.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Crea ErrorResponse y expone sus componentes")
    void shouldCreateAndExposeComponents() {
        ErrorResponse er = new ErrorResponse(
                "Conflict",
                "Ya existe un recurso",
                "/api/v1/usuarios",
                409,
                "2025-08-30T02:24:46.819150400Z"
        );

        assertThat(er.error()).isEqualTo("Conflict");
        assertThat(er.message()).isEqualTo("Ya existe un recurso");
        assertThat(er.path()).isEqualTo("/api/v1/usuarios");
        assertThat(er.status()).isEqualTo(409);
        assertThat(er.timestamp()).isEqualTo("2025-08-30T02:24:46.819150400Z");
    }

    @Test
    @DisplayName("Equals y hashCode siguen semántica por valor (propia del record)")
    void equalsAndHashCodeByValue() {
        ErrorResponse a = new ErrorResponse("Conflict", "m1", "/p", 409, "t1");
        ErrorResponse b = new ErrorResponse("Conflict", "m1", "/p", 409, "t1");
        ErrorResponse c = new ErrorResponse("Bad Request", "m2", "/q", 400, "t2");

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b).isNotEqualTo(c);
    }

    @Test
    @DisplayName("toString contiene los campos clave (útil para logging)")
    void toStringContainsKeyFields() {
        ErrorResponse er = new ErrorResponse("Bad Request", "m", "/p", 400, "t");
        String s = er.toString();

        assertThat(s)
                .contains("ErrorResponse")
                .contains("Bad Request")
                .contains("m")
                .contains("/p")
                .contains("400")
                .contains("t");
    }

    @Test
    @DisplayName("Serializa a JSON con los nombres de campo esperados")
    void serializeToJson() throws Exception {
        ErrorResponse er = new ErrorResponse(
                "Unauthorized",
                "Token inválido",
                "/api/v1/secure",
                401,
                "2025-09-05T12:00:00Z"
        );

        String json = mapper.writeValueAsString(er);
        JsonNode node = mapper.readTree(json);

        assertThat(node.get("error").asText()).isEqualTo("Unauthorized");
        assertThat(node.get("message").asText()).isEqualTo("Token inválido");
        assertThat(node.get("path").asText()).isEqualTo("/api/v1/secure");
        assertThat(node.get("status").asInt()).isEqualTo(401);
        assertThat(node.get("timestamp").asText()).isEqualTo("2025-09-05T12:00:00Z");
    }

    @Test
    @DisplayName("Deserializa desde JSON a un ErrorResponse equivalente")
    void deserializeFromJson() throws Exception {
        String json = """
                {
                  "error": "Internal Server Error",
                  "message": "Ha ocurrido un error",
                  "path": "/api/v1/ops",
                  "status": 500,
                  "timestamp": "2025-09-05T18:30:00Z"
                }
                """;

        ErrorResponse er = mapper.readValue(json, ErrorResponse.class);

        assertThat(er.error()).isEqualTo("Internal Server Error");
        assertThat(er.message()).isEqualTo("Ha ocurrido un error");
        assertThat(er.path()).isEqualTo("/api/v1/ops");
        assertThat(er.status()).isEqualTo(500);
        assertThat(er.timestamp()).isEqualTo("2025-09-05T18:30:00Z");
    }

    @Test
    @DisplayName("Permite nulos en campos y los (de)serializa como null")
    void handlesNullValues() throws JsonProcessingException {
        ErrorResponse er = new ErrorResponse(
                null,
                null,
                "/api/v1/health",
                null,
                null
        );

        String json = mapper.writeValueAsString(er);
        JsonNode node = mapper.readTree(json);

        assertThat(node.get("error").isNull()).isTrue();
        assertThat(node.get("message").isNull()).isTrue();
        assertThat(node.get("path").asText()).isEqualTo("/api/v1/health");
        assertThat(node.get("status").isNull()).isTrue();
        assertThat(node.get("timestamp").isNull()).isTrue();

        ErrorResponse back = mapper.readValue(json, ErrorResponse.class);
        assertThat(back).isEqualTo(er);
    }
}