package co.com.pragma.crediya.consumer.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestConsumerConfigTest {

    @Test
    @DisplayName("getWebClient: usa baseUrl, header Content-Type JSON, y ReactorClientHttpConnector")
    void getWebClient_buildsClientWithExpectedConfig() {

        String url = "https://api.example.com";
        int timeoutMillis = 3_000;

        RestConsumerConfig config = new RestConsumerConfig(url, timeoutMillis);

        WebClient.Builder builder = mock(WebClient.Builder.class, RETURNS_SELF);
        WebClient expectedClient = mock(WebClient.class);
        when(builder.build()).thenReturn(expectedClient);

        ArgumentCaptor<ClientHttpConnector> connectorCaptor = ArgumentCaptor.forClass(ClientHttpConnector.class);

        WebClient client = config.getWebClient(builder);

        assertThat(client).isSameAs(expectedClient);

        verify(builder).baseUrl(url);
        verify(builder).defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        verify(builder).clientConnector(connectorCaptor.capture());
        verify(builder).build();
        verifyNoMoreInteractions(builder);

        ClientHttpConnector connector = connectorCaptor.getValue();
        assertThat(connector)
                .isNotNull()
                .isInstanceOf(ReactorClientHttpConnector.class);
    }

    @Test
    @DisplayName("getWebClient: no añade headers/params adicionales inesperados en el builder")
    void getWebClient_doesNotMutateBuilderUnexpectedly() {

        RestConsumerConfig config = new RestConsumerConfig("http://localhost:8080", 1_000);
        WebClient.Builder builder = mock(WebClient.Builder.class, RETURNS_SELF);
        when(builder.build()).thenReturn(mock(WebClient.class));

        config.getWebClient(builder);

        verify(builder).baseUrl("http://localhost:8080");
        verify(builder).defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        verify(builder).clientConnector(any(ClientHttpConnector.class));
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }
}