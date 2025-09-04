package co.com.pragma.crediya.api.exceptions;

import co.com.pragma.crediya.api.helper.ExceptionHelper;
import co.com.pragma.crediya.api.helper.JsonErrorMessageFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    // JSON keys (evita “magic strings”)
    private static final String K_ERROR = "error";
    private static final String K_MESSAGE = "message";
    private static final String K_PATH = "path";
    private static final String K_STATUS = "status";
    private static final String K_TIMESTAMP = "timestamp";

    private static final String DEFAULT_CLIENT_MESSAGE = "Ha ocurrido un error";
    private static final String DEFAULT_BAD_REQUEST_MESSAGE = "Solicitud inválida.";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {

        final Throwable ex = ExceptionHelper.unwrap(getError(request));

        final HttpStatus status = ExceptionHelper.resolveStatus(ex);

        final String message = resolveClientMessage(ex, status);

        final Map<String, Object> body = LinkedHashMap.newLinkedHashMap(6);
        body.put(K_ERROR, status.getReasonPhrase());
        body.put(K_MESSAGE, message);
        body.put(K_PATH, request.path());
        body.put(K_STATUS, status.value());
        body.put(K_TIMESTAMP, Instant.now().toString());
        return body;
    }

    private String resolveClientMessage(Throwable ex, HttpStatus status) {
        if (JsonErrorMessageFactory.isJsonDecodeError(ex)) {
            return JsonErrorMessageFactory.build(ex);
        }
        if (ex != null && ex.getMessage() != null && !ex.getMessage().isBlank()) {
            return ex.getMessage();
        }

        return status.is4xxClientError() ? DEFAULT_BAD_REQUEST_MESSAGE : DEFAULT_CLIENT_MESSAGE;
    }


}
