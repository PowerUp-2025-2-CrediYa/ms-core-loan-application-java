package co.com.pragma.crediya.api.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApiError(

        @Schema(example = "Conflict")
        String error,

        @Schema(example = "Ya existe un usuario con el email: ")
        String message,

        @Schema(example = "/api/v1/usuarios")
        String path,

        @Schema(example = "409")
        Integer status,

        @Schema(example = "2025-08-30T02:24:46.819150400Z")
        String timestamp

) {}