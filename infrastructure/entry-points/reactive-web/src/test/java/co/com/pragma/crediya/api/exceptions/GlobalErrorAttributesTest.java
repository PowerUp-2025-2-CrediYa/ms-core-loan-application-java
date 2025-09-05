package co.com.pragma.crediya.api.exceptions;

import co.com.pragma.crediya.api.helper.ExceptionHelper;
import co.com.pragma.crediya.api.helper.JsonErrorMessageFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class GlobalErrorAttributesTest {

    private GlobalErrorAttributes errorAttributes;

    private MockedStatic<ExceptionHelper> exceptionHelperStatic;
    private MockedStatic<JsonErrorMessageFactory> jsonMessageFactoryStatic;

    @BeforeEach
    void setUp() {
        errorAttributes = new GlobalErrorAttributes();
        exceptionHelperStatic = mockStatic(ExceptionHelper.class);
        jsonMessageFactoryStatic = mockStatic(JsonErrorMessageFactory.class);
    }

    @AfterEach
    void tearDown() {
        if (exceptionHelperStatic != null) exceptionHelperStatic.close();
        if (jsonMessageFactoryStatic != null) jsonMessageFactoryStatic.close();
    }

    private static ServerRequest requestWithError(String path, Throwable ex) {
        MockServerHttpRequest httpRequest = MockServerHttpRequest.get(path).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(httpRequest);

        String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
        exchange.getAttributes().put(ERROR_ATTRIBUTE, ex);

        return ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
    }

    private static void assertCommon(Map<String, Object> body,
                                     HttpStatus expectedStatus,
                                     String expectedPath,
                                     String expectedMessage) {
        assertThat(body).isNotNull();
        assertThat(body.get("error")).isEqualTo(expectedStatus.getReasonPhrase());
        assertThat(body.get("status")).isEqualTo(expectedStatus.value());
        assertThat(body.get("path")).isEqualTo(expectedPath);
        assertThat(body.get("message")).isEqualTo(expectedMessage);

        Object ts = body.get("timestamp");
        assertThat(ts).isInstanceOf(String.class);

        assertDoesNotThrow(() -> Instant.parse((String) ts));
    }

    @Nested
    @DisplayName("getErrorAttributes")
    class GetErrorAttributes {

        @Test
        @DisplayName("Cuando es error de decodificación JSON, usa el mensaje generado por JsonErrorMessageFactory")
        void jsonDecodeError_usesFactoryMessage() {

            Throwable ex = new RuntimeException("raw json error");
            ServerRequest request = requestWithError("/api/v1/usuarios", ex);

            exceptionHelperStatic.when(() -> ExceptionHelper.unwrap(any())).thenReturn(ex);
            exceptionHelperStatic.when(() -> ExceptionHelper.resolveStatus(ex)).thenReturn(HttpStatus.BAD_REQUEST);

            jsonMessageFactoryStatic.when(() -> JsonErrorMessageFactory.isJsonDecodeError(ex)).thenReturn(true);
            jsonMessageFactoryStatic.when(() -> JsonErrorMessageFactory.build(ex)).thenReturn("JSON inválido en campo 'email'");

            Map<String, Object> body = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

            assertCommon(body, HttpStatus.BAD_REQUEST, "/api/v1/usuarios", "JSON inválido en campo 'email'");
        }

        @Test
        @DisplayName("Cuando la excepción trae mensaje no vacío, devuelve ese mensaje")
        void exceptionWithMessage_usesExceptionMessage() {

            Throwable ex = new IllegalArgumentException("Documento inválido");
            ServerRequest request = requestWithError("/api/v1/usuarios", ex);

            exceptionHelperStatic.when(() -> ExceptionHelper.unwrap(any())).thenReturn(ex);
            exceptionHelperStatic.when(() -> ExceptionHelper.resolveStatus(ex)).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);

            jsonMessageFactoryStatic.when(() -> JsonErrorMessageFactory.isJsonDecodeError(ex)).thenReturn(false);

            Map<String, Object> body = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

            assertCommon(body, HttpStatus.UNPROCESSABLE_ENTITY, "/api/v1/usuarios", "Documento inválido");
        }

        @Test
        @DisplayName("Cuando no hay mensaje y es 4xx, usa 'Solicitud inválida.'")
        void noMessage_4xx_usesDefaultBadRequestMessage() {
            Throwable ex = new RuntimeException(); // sin mensaje
            ServerRequest request = requestWithError("/api/v1/usuarios", ex);

            exceptionHelperStatic.when(() -> ExceptionHelper.unwrap(any())).thenReturn(ex);
            exceptionHelperStatic.when(() -> ExceptionHelper.resolveStatus(ex)).thenReturn(HttpStatus.BAD_REQUEST);

            jsonMessageFactoryStatic.when(() -> JsonErrorMessageFactory.isJsonDecodeError(ex)).thenReturn(false);

            Map<String, Object> body = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

            assertCommon(body, HttpStatus.BAD_REQUEST, "/api/v1/usuarios", "Solicitud inválida.");
        }

        @Test
        @DisplayName("Cuando no hay mensaje y es 5xx, usa 'Ha ocurrido un error'")
        void noMessage_5xx_usesDefaultClientMessage() {
            Throwable ex = new RuntimeException(); // sin mensaje
            ServerRequest request = requestWithError("/api/v1/usuarios", ex);

            exceptionHelperStatic.when(() -> ExceptionHelper.unwrap(any())).thenReturn(ex);
            exceptionHelperStatic.when(() -> ExceptionHelper.resolveStatus(ex)).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

            jsonMessageFactoryStatic.when(() -> JsonErrorMessageFactory.isJsonDecodeError(ex)).thenReturn(false);

            Map<String, Object> body = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

            assertCommon(body, HttpStatus.INTERNAL_SERVER_ERROR, "/api/v1/usuarios", "Ha ocurrido un error");
        }
    }
}