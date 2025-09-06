package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.r2dbc.entity.LoanApplicatonEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class LoanReactiveRepositoryAdapterTest {
    @Mock
    private LoanReactiveRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    private LoanReactiveRepositoryAdapter adapter;

    private LoanApplication loanApplication;
    private LoanApplicatonEntity entity;

    @BeforeEach
    void setUp() {
        adapter = new LoanReactiveRepositoryAdapter(repository, objectMapper, transactionalOperator);

        loanApplication = LoanApplication.builder()
                .documentId("123456789")
                .loanType("PERSONAL")
                .loanTerm(12)
                .amount(BigDecimal.valueOf(5000000))
                .build();

        entity = new LoanApplicatonEntity(); // simula entidad si es necesario
    }

    @Test
    void shouldMapDataIntegrityViolationExceptionToLoanDBException() {
        DataIntegrityViolationException dbException = new DataIntegrityViolationException("Duplicate key");
        Mockito.when(repository.save(Mockito.any())).thenReturn(Mono.error(dbException));
        Mockito.when(transactionalOperator.transactional(Mockito.any(Mono.class))).thenAnswer(inv -> inv.getArgument(0))
                .thenAnswer(invocation -> invocation.getArgument(0));


        StepVerifier.create(adapter.saveLoanApplication(loanApplication))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().contains("Duplicate key") // ajusta según tu lógica en valideDBException
                )
                .verify();
    }

}
