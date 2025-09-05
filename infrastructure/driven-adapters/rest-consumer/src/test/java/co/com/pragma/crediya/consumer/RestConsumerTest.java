package co.com.pragma.crediya.consumer;

import co.com.pragma.crediya.model.loanapplication.User;
import co.com.pragma.crediya.model.loanapplication.exception.UserNotExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestConsumerTest {

    private WebClient.Builder mockBuilder;
    private WebClient mockClient;

    private RequestHeadersUriSpec<?> mockGetSpec;
    private RequestHeadersSpec<?> mockHeadersSpec;
    private ResponseSpec mockResponseSpec;

    private MockedStatic<WebClient> webClientStatic;

    private RestConsumer restConsumer;

    @BeforeEach
    @SuppressWarnings({"rawtypes", "unchecked"})
    void setUp() {
        mockBuilder = mock(WebClient.Builder.class, RETURNS_SELF);
        mockClient = mock(WebClient.class);
        mockGetSpec = (RequestHeadersUriSpec) mock(RequestHeadersUriSpec.class);
        mockHeadersSpec = (RequestHeadersSpec) mock(RequestHeadersSpec.class);
        mockResponseSpec = mock(ResponseSpec.class);

        webClientStatic = mockStatic(WebClient.class);
        webClientStatic.when(WebClient::builder).thenReturn(mockBuilder);

        when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockClient);

        doReturn(mockGetSpec).when(mockClient).get();
        doReturn(mockHeadersSpec).when(mockGetSpec).uri(any(Function.class));
        doReturn(mockHeadersSpec).when(mockHeadersSpec).accept(any(MediaType[].class));
        doReturn(mockResponseSpec).when(mockHeadersSpec).retrieve();

        restConsumer = new RestConsumer(new ObjectMapper(), "http://fake-host");
    }

    @AfterEach
    void tearDown() {
        if (webClientStatic != null) {
            webClientStatic.close();
        }
    }

    @Test
    @DisplayName("findUserByDocumentId: 200 OK -> devuelve User")
    void shouldReturnUserWhenServiceReturns200() {
        String documentId = "123456789";
        User expected = User.builder()
                .firstName("Carlos")
                .lastName("Galeano")
                .email("carlos@email.com")
                .documentId(documentId)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Calle 123 #45-67")
                .phoneNumber("3001234567")
                .baseSalary(2_000_000.0)
                .build();

        when(mockResponseSpec.onStatus(any(), any())).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(User.class)).thenReturn(Mono.just(expected));

        Mono<User> result = restConsumer.findUserByDocumentId(documentId);

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u != null
                                && documentId.equals(u.getDocumentId())
                                && "Carlos".equals(u.getFirstName())
                                && "Galeano".equals(u.getLastName())
                                && "carlos@email.com".equals(u.getEmail())
                )
                .verifyComplete();

        verify(mockBuilder).baseUrl("http://fake-host");
        verify(mockBuilder).build();
        verify(mockClient).get();
        verify(mockGetSpec).uri(any(Function.class));
        verify(mockHeadersSpec).accept(any(MediaType[].class));
        verify(mockHeadersSpec).retrieve();
        verify(mockResponseSpec).onStatus(any(), any());
        verify(mockResponseSpec).bodyToMono(User.class);
        verifyNoMoreInteractions(mockResponseSpec);
    }

    @Test
    @DisplayName("findUserByDocumentId: 4xx -> UserNotExistsException con mensaje backend")
    void shouldErrorWithUserNotExistsWhen4xx() {
        String documentId = "999999";
        String backendMessage = "El usuario con identificación: 178822540 no existe";

        when(mockResponseSpec.onStatus(any(), any())).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(User.class))
                .thenReturn(Mono.error(new UserNotExistsException(backendMessage)));

        Mono<User> result = restConsumer.findUserByDocumentId(documentId);

        StepVerifier.create(result)
                .expectErrorMatches(t ->
                        t instanceof UserNotExistsException &&
                                backendMessage.equals(t.getMessage())
                )
                .verify();

        verify(mockClient).get();
        verify(mockGetSpec).uri(any(Function.class));
        verify(mockHeadersSpec).accept(any(MediaType[].class));
        verify(mockHeadersSpec).retrieve();
        verify(mockResponseSpec).onStatus(any(), any());
        verify(mockResponseSpec).bodyToMono(User.class);
        verifyNoMoreInteractions(mockResponseSpec);
    }
}