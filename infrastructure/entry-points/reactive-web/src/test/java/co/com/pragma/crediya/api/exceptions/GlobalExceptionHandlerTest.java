package co.com.pragma.crediya.api.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private ErrorAttributes errorAttributes;
    private WebProperties webProperties;
    private ApplicationContext applicationContext;
    private ServerCodecConfigurer codecConfigurer;

    private GlobalExceptionHandler handler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        errorAttributes = mock(ErrorAttributes.class);

        webProperties = new WebProperties();

        applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getClassLoader()).thenReturn(getClass().getClassLoader());

        codecConfigurer = ServerCodecConfigurer.create();

        handler = new GlobalExceptionHandler(errorAttributes, webProperties, applicationContext, codecConfigurer);

        RouterFunction<?> router = handler.getRoutingFunction(errorAttributes);
        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Nested
    @DisplayName("renderErrorResponse")
    class RenderErrorResponse {

        @Test
        @DisplayName("Devuelve 400 con JSON body cuando la excepción es ResponseStatusException(BAD_REQUEST)")
        void returns400WithMappedBody() {

            Throwable ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Documento inválido");

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("error", "Bad Request");
            body.put("message", "Documento inválido");
            body.put("path", "/api/v1/usuarios");
            body.put("status", 400);
            body.put("timestamp", Instant.now().toString());

            when(errorAttributes.getError(any(ServerRequest.class))).thenReturn(ex);
            when(errorAttributes.getErrorAttributes(any(ServerRequest.class), any(ErrorAttributeOptions.class)))
                    .thenReturn(body);

            webTestClient.get()
                    .uri("/api/v1/usuarios?documentId=123")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isEqualTo("Bad Request")
                    .jsonPath("$.message").isEqualTo("Documento inválido")
                    .jsonPath("$.path").isEqualTo("/api/v1/usuarios")
                    .jsonPath("$.status").isEqualTo(400)
                    .jsonPath("$.timestamp").exists();

            verify(errorAttributes).getError(any(ServerRequest.class));
            verify(errorAttributes).getErrorAttributes(any(ServerRequest.class), any(ErrorAttributeOptions.class));
        }

        @Test
        @DisplayName("Devuelve 500 con JSON body cuando la excepción es RuntimeException (fallback)")
        void returns500WithMappedBody() {
            Throwable ex = new RuntimeException("Fallo inesperado");

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("error", "Internal Server Error");
            body.put("message", "Ha ocurrido un error");
            body.put("path", "/api/v1/solicitudes");
            body.put("status", 500);
            body.put("timestamp", Instant.now().toString());

            when(errorAttributes.getError(any(ServerRequest.class))).thenReturn(ex);
            when(errorAttributes.getErrorAttributes(any(ServerRequest.class), any(ErrorAttributeOptions.class)))
                    .thenReturn(body);

            webTestClient.post()
                    .uri("/api/v1/solicitudes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isEqualTo("Internal Server Error")
                    .jsonPath("$.message").isEqualTo("Ha ocurrido un error")
                    .jsonPath("$.path").isEqualTo("/api/v1/solicitudes")
                    .jsonPath("$.status").isEqualTo(500)
                    .jsonPath("$.timestamp").exists();

            verify(errorAttributes).getError(any(ServerRequest.class));
            verify(errorAttributes).getErrorAttributes(any(ServerRequest.class), any(ErrorAttributeOptions.class));
        }
    }
}