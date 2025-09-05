package co.com.pragma.crediya.consumer;

import co.com.pragma.crediya.consumer.model.ErrorResponse;
import co.com.pragma.crediya.model.loanapplication.User;
import co.com.pragma.crediya.model.loanapplication.exception.UserNotExistsException;
import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer  implements LoanApplicationRestClient{

    private final WebClient restClient;

    @Autowired
    public RestConsumer(ObjectMapper mapper,
                        @Value("${adapter.restconsumer.url}") String url) {
        this.restClient = WebClient.builder().baseUrl(url).build();
    }

    @Override
    @CircuitBreaker(name = "findUserByDocumentId")
    public Mono<User> findUserByDocumentId(String documentId) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/usuarios")
                        .queryParam("documentId", documentId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(ErrorResponse.class)
                                .map(error -> new UserNotExistsException(error.message()))
                )
                .bodyToMono(User.class);
    }
}

