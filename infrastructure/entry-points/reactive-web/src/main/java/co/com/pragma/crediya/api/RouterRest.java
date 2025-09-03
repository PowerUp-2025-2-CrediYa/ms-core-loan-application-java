package co.com.pragma.crediya.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    public static final String BASE_URL = "/api/v1/solicitud";

    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.POST,
                    beanClass = LoanHandler.class,
                    beanMethod = "listenPOSTUseCase",
                    operation = @Operation(
                            summary = "Crear usuario"
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(LoanHandler handler) {
        return route(POST(BASE_URL), handler::listenPOSTUseCase);

    }
}
