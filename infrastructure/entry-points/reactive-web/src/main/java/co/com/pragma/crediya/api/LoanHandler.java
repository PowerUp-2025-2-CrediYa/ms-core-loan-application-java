package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.mapper.LoanMapper;
import co.com.pragma.crediya.api.mapper.LoanResponseMapper;
import co.com.pragma.crediya.api.model.request.LoanApplicationRequest;
import co.com.pragma.crediya.api.model.response.ApiError;
import co.com.pragma.crediya.api.model.response.LoanApplicationResponse;
import co.com.pragma.crediya.usecase.loanapplication.LoanApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Tag(name = "Gestión de Solicitudes de Prestamo", description = "Operaciones relacionadas con solicitudes de prestamo")
public class LoanHandler {

    private final LoanApplicationUseCase loanApplicationUseCase;
    private static final Logger log = LoggerFactory.getLogger(LoanHandler.class);


    @Operation(
            summary = "Crear solicitud de prestamo",
            description = "Recibe la solicitud y la guarda en el sistema",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoanApplicationRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente",
                            content = @Content(schema = @Schema(implementation = LoanApplicationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos",
                            content = @Content(schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "error": "Bad Request",
                                                      "message": "El numero de documento no puede estar vacío",
                                                      "path": "/api/v1/solicitud",
                                                      "status": 400,
                                                      "timestamp": "2025-08-30T02:39:08.855306500Z"
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "422", description = "Violación de reglas de negocio",
                            content = @Content(schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "error": "Unprocessable Entity",
                                                      "message": "El monto no esta dentro del rango permitido",
                                                      "path": "/api/v1/solicitud",
                                                      "status": 422,
                                                      "timestamp": "2025-08-30T02:41:02.238173500Z"
                                                    }
                                                    """
                                    )))
            }
    )

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return

                serverRequest.bodyToMono(LoanApplicationRequest.class)
                        .map(LoanMapper::toDomain)
                        .flatMap(loanApplicationUseCase::saveUser)
                        .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(LoanResponseMapper.fromDomain(saved)));

    }
}
