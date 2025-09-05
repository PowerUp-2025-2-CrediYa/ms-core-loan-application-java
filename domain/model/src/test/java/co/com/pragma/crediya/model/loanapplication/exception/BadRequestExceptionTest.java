package co.com.pragma.crediya.model.loanapplication.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class BadRequestExceptionTest {

    @Test
    @DisplayName("Guarda y expone el mensaje pasado al constructor")
    void shouldStoreMessage() {
        String msg = "Documento inválido";
        BadRequestException ex = new BadRequestException(msg);

        assertThat(ex.getMessage()).isEqualTo(msg);
    }

    @Test
    @DisplayName("Es una RuntimeException (unchecked)")
    void shouldBeRuntimeException() {
        BadRequestException ex = new BadRequestException("x");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("No tiene causa por defecto")
    void causeShouldBeNullByDefault() {
        BadRequestException ex = new BadRequestException("x");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("toString contiene el nombre de la clase y el mensaje")
    void toStringContainsClassAndMessage() {
        BadRequestException ex = new BadRequestException("Documento inválido");
        String s = ex.toString();

        assertThat(s)
                .contains(BadRequestException.class.getName())
                .contains("Documento inválido");
    }

    @Test
    @DisplayName("Se propaga correctamente en un flujo reactivo (WebFlux)")
    void propagatesInReactivePipeline() {
        String msg = "Parámetros inválidos";
        Mono<String> pipeline = Mono.defer(() -> Mono.error(new BadRequestException(msg)));

        StepVerifier.create(pipeline)
                .expectErrorSatisfies(t ->
                        assertThat(t)
                                .isInstanceOf(BadRequestException.class)
                                .hasMessage(msg)
                )
                .verify();
    }
}
