package co.com.pragma.crediya.api.helper;

import co.com.pragma.crediya.model.loanapplication.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExceptionHelperTest {

    @Test
    @DisplayName("Debe retornar CONFLICT para excepciones de dominio")
    void shouldReturnConflictForDomainExceptions() {
        assertEquals(HttpStatus.CONFLICT, ExceptionHelper.resolveStatus(new InvalidAmountRangeException("Monto fuera del rango: 0")));
        assertEquals(HttpStatus.CONFLICT, ExceptionHelper.resolveStatus(new InvalidLoanTermException("Plazo Invalido: 0")));
        assertEquals(HttpStatus.CONFLICT, ExceptionHelper.resolveStatus(new LoanTypeNotExistsException("No existe el tipo de prestamo: HIPOTECARIOS")));
    }

    @Test
    @DisplayName("Debe retornar UNPROCESSABLE_ENTITY para UserNotExistsException")
    void shouldReturnUnprocessableEntityForUserNotExistsException() {
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ExceptionHelper.resolveStatus(new UserNotExistsException("Usuario no existe")));
    }

    @Test
    @DisplayName("Debe retornar BAD_REQUEST para errores de JSON o InvalidLoanException")
    void shouldReturnBadRequestForJsonDecodeErrorOrInvalidLoanException() {
        Throwable jsonError = new RuntimeException("Json decode error");

        assertEquals(HttpStatus.BAD_REQUEST, ExceptionHelper.resolveStatus(new InvalidLoanException("Datos inválidos")));
    }

    @Test
    @DisplayName("Debe retornar INTERNAL_SERVER_ERROR para excepciones desconocidas")
    void shouldReturnInternalServerErrorForUnknownException() {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionHelper.resolveStatus(new RuntimeException("Error inesperado")));
    }

    @Test
    @DisplayName("Debe retornar el status de ResponseStatusException")
    void shouldReturnStatusFromResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontrado");
        assertEquals(HttpStatus.NOT_FOUND, ExceptionHelper.resolveStatus(ex));
    }

    @Test
    @DisplayName("Debe desempaquetar correctamente una excepción anidada")
    void shouldUnwrapNestedException() {
        Throwable cause = new InvalidLoanException("Causa original");
        Throwable wrapped = new RuntimeException("Wrapper", cause);

        Throwable result = ExceptionHelper.unwrap(wrapped);
        assertEquals(cause, result);
    }

    @Test
    @DisplayName("Debe retornar null si se desempaqueta una excepción nula")
    void shouldReturnNullWhenUnwrappingNull() {
        assertNull(ExceptionHelper.unwrap(null));
    }
}
