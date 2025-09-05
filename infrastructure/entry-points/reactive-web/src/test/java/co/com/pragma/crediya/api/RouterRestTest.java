package co.com.pragma.crediya.api;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    private WebTestClient webTestClient;

    @Mock
    private LoanHandler loanHandler;

    @BeforeEach
    void setUp() {
        RouterFunction<ServerResponse> routerFunction = new RouterRest().routerFunction(loanHandler);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void shouldRoutePostRequestToLoanHandler() {

        LoanApplication loanApplication = LoanApplication.builder()
                .documentId("123456789")
                .loanType("PERSONAL")
                .loanTerm(12)
                .amount(BigDecimal.valueOf(5000000))
                .build();

        ServerResponse mockResponse = ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Solicitud creada exitosamente")
                .block();

        Mockito.when(loanHandler.listenPOSTUseCase(Mockito.any()))
                .thenReturn(Mono.just(mockResponse));

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loanApplication)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .isEqualTo("Solicitud creada exitosamente");
    }

}
