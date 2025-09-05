package co.com.pragma.crediya.api.mapper;


import co.com.pragma.crediya.api.LoanHandler;
import co.com.pragma.crediya.api.model.request.LoanApplicationRequest;
import co.com.pragma.crediya.api.model.response.LoanApplicationResponse;
import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.usecase.loanapplication.LoanApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoanHandlerTest {

    private LoanApplicationUseCase loanApplicationUseCase;
    private LoanHandler loanHandler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        loanApplicationUseCase = mock(LoanApplicationUseCase.class);
        loanHandler = new LoanHandler(loanApplicationUseCase);

        RouterFunction<ServerResponse> route = RouterFunctions.route(
                RequestPredicates.POST("/api/v1/solicitud").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                loanHandler::listenPOSTUseCase
        );

        webTestClient = WebTestClient.bindToRouterFunction(route).build();
    }

    @Test
    @DisplayName("should handle POST request and return 201 with LoanApplicationResponse")
    void shouldHandlePostRequestSuccessfully() {

        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .documentId("123456789")
                .amount(BigDecimal.valueOf(10000000))
                .loanType("PERSONAL")
                .loanTerm(12)
                .build();

        LoanApplication domain = LoanMapper.toDomain(request);

        when(loanApplicationUseCase.saveUser(any(LoanApplication.class)))
                .thenReturn(Mono.just(domain));

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LoanApplicationResponse.class)
                .consumeWith(result -> {
                    LoanApplicationResponse response = result.getResponseBody();
                    assert response != null;
                    assert response.getDocumentId().equals("123456789");
                });

        verify(loanApplicationUseCase).saveUser(any(LoanApplication.class));
    }
}
