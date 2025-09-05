package co.com.pragma.crediya.usecase.loanapplication;

import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRestClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Configuración de testing para LoanApplicationUseCase
 * 
 * Esta clase proporciona beans de prueba mockeados para el contexto de Spring
 * siguiendo el principio de Dependency Inversion (DIP)
 */
@TestConfiguration
public class LoanApplicationUseCaseTestConfiguration {

    /**
     * Bean mockeado para LoanApplicationRepository
     * 
     * @return Mock de LoanApplicationRepository
     */
    @Bean
    @Primary
    public LoanApplicationRepository loanApplicationRepository() {
        return mock(LoanApplicationRepository.class);
    }

    /**
     * Bean mockeado para LoanApplicationRestClient
     * 
     * @return Mock de LoanApplicationRestClient
     */
    @Bean
    @Primary
    public LoanApplicationRestClient loanApplicationRestClient() {
        return mock(LoanApplicationRestClient.class);
    }
}

