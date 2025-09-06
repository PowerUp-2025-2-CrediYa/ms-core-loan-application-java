package co.com.pragma.crediya.api.model.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Crea ApiError y expone los componentes correctamente")
    void shouldCreateApiErrorAndExposeComponents() {
        ApiError error = new ApiError(
                "Conflict",
                "Ya existe un usuario con el email: test@acme.com",
                "/api/v1/usuarios",
                409,
                "2025-08-30T02:24:46.819150400Z"
        );

        assertThat(error.error()).isEqualTo("Conflict");
        assertThat(error.message()).isEqualTo("Ya existe un usuario con el email: test@acme.com");
        assertThat(error.path()).isEqualTo("/api/v1/usuarios");
        assertThat(error.status()).isEqualTo(409);
        assertThat(error.timestamp()).isEqualTo("2025-08-30T02:24:46.819150400Z");
    }

    @Test
    @DisplayName("Equals y hashCode deben operar por valor (semántica de record)")
    void shouldImplementEqualsAndHashCodeByValue() {
        ApiError a = new ApiError("Conflict", "m1", "/p", 409, "t1");
        ApiError b = new ApiError("Conflict", "m1", "/p", 409, "t1");
        ApiError c = new ApiError("Bad Request", "m2", "/q", 400, "t2");

        assertThat(a)
                .isEqualTo(b)
                .hasSameHashCodeAs(b)
                .isNotEqualTo(c);
    }

    @Test
    @DisplayName("toString debe contener los campos clave para facilitar el logging")
    void toStringShouldContainKeyFields() {
        ApiError error = new ApiError("Conflict", "m", "/p", 409, "t");
        String s = error.toString();

        assertThat(s)
                .contains("ApiError")
                .contains("Conflict")
                .contains("m")
                .contains("/p")
                .contains("409")
                .contains("t");
    }

    @Test
    @DisplayName("Serializa a JSON con los nombres de campo esperados")
    void shouldSerializeToJson() throws Exception {
        ApiError error = new ApiError(
                "Conflict",
                "Ya existe un usuario",
                "/api/v1/usuarios",
                409,
                "2025-08-30T02:24:46.819150400Z"
        );

        String json = mapper.writeValueAsString(error);
        JsonNode node = mapper.readTree(json);

        assertThat(node.get("error").asText()).isEqualTo("Conflict");
        assertThat(node.get("message").asText()).isEqualTo("Ya existe un usuario");
        assertThat(node.get("path").asText()).isEqualTo("/api/v1/usuarios");
        assertThat(node.get("status").asInt()).isEqualTo(409);
        assertThat(node.get("timestamp").asText()).isEqualTo("2025-08-30T02:24:46.819150400Z");
    }

    @Test
    @DisplayName("Deserializa desde JSON a un ApiError equivalente")
    void shouldDeserializeFromJson() throws Exception {
        String json = """
                {
                  "error": "Bad Request",
                  "message": "Documento inválido",
                  "path": "/api/v1/usuarios",
                  "status": 400,
                  "timestamp": "2025-09-05T12:00:00Z"
                }
                """;

        ApiError error = mapper.readValue(json, ApiError.class);

        assertThat(error.error()).isEqualTo("Bad Request");
        assertThat(error.message()).isEqualTo("Documento inválido");
        assertThat(error.path()).isEqualTo("/api/v1/usuarios");
        assertThat(error.status()).isEqualTo(400);
        assertThat(error.timestamp()).isEqualTo("2025-09-05T12:00:00Z");
    }

    @Test
    @DisplayName("Permite valores nulos en campos opcionales y los (de)serializa como null")
    void shouldHandleNullables() throws JsonProcessingException {
        ApiError error = new ApiError(
                null,
                null,
                "/api/v1/usuarios",
                500,
                null
        );

        String json = mapper.writeValueAsString(error);
        JsonNode node = mapper.readTree(json);

        assertThat(node.get("error").isNull()).isTrue();
        assertThat(node.get("message").isNull()).isTrue();
        assertThat(node.get("path").asText()).isEqualTo("/api/v1/usuarios");
        assertThat(node.get("status").asInt()).isEqualTo(500);
        assertThat(node.get("timestamp").isNull()).isTrue();

        ApiError back = mapper.readValue(json, ApiError.class);
        assertThat(back).isEqualTo(error);
    }
}