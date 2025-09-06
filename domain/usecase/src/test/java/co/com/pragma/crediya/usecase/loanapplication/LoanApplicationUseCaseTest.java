package co.com.pragma.crediya.usecase.loanapplication;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.LoanStatusCode;
import co.com.pragma.crediya.model.loanapplication.User;
import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRestClient;
import co.com.pragma.crediya.model.loanapplication.helper.LoanApplicationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para LoanApplicationUseCase
 * 
 * Estas pruebas siguen los principios SOLID y las mejores prácticas de testing:
 * - Single Responsibility: Cada test verifica un comportamiento específico
 * - Open/Closed: Fácil extensión para nuevos casos de prueba
 * - Dependency Inversion: Uso de mocks para las dependencias
 * - Clean Code: Nombres descriptivos y estructura clara
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoanApplicationUseCase - Pruebas Unitarias")
class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepositoryGateway;

    @Mock
    private LoanApplicationRestClient loanRestClientGateway;

    @InjectMocks
    private LoanApplicationUseCase loanApplicationUseCase;

    private LoanApplication validLoanApplication;
    private User validUser;

    @BeforeEach
    void setUp() {
        validLoanApplication = LoanApplication.builder()
                .loanApplicationId(UUID.randomUUID())
                .documentId("12345678")
                .amount(new BigDecimal("1000000"))
                .loanType("PERSONAL")
                .loanTerm(12)
                .createAt(LocalDate.now())
                .build();

        validUser = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan.perez@email.com")
                .documentId("12345678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Calle 123 #45-67")
                .phoneNumber("3001234567")
                .baseSalary(2000000.0)
                .build();
    }

    @Test
    @DisplayName("Debería guardar una solicitud de préstamo exitosamente cuando el usuario existe y la validación es exitosa")
    void shouldSaveLoanApplicationSuccessfullyWhenUserExistsAndValidationPasses() {
        LoanApplication expectedSavedLoan = validLoanApplication.toBuilder()
                .loanStatus(LoanStatusCode.PENDING_REVIEW)
                .build();

        when(loanRestClientGateway.findUserByDocumentId(validLoanApplication.getDocumentId()))
                .thenReturn(Mono.just(validUser));
        when(loanApplicationRepositoryGateway.saveLoanApplication(any(LoanApplication.class)))
                .thenReturn(Mono.just(expectedSavedLoan));

        StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
                .expectNext(expectedSavedLoan)
                .verifyComplete();

        verify(loanRestClientGateway).findUserByDocumentId(validLoanApplication.getDocumentId());
        verify(loanApplicationRepositoryGateway).saveLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería establecer el estado como PENDING_REVIEW antes de guardar")
    void shouldSetLoanStatusAsPendingReviewBeforeSaving() {
        when(loanRestClientGateway.findUserByDocumentId(validLoanApplication.getDocumentId()))
                .thenReturn(Mono.just(validUser));
        when(loanApplicationRepositoryGateway.saveLoanApplication(any(LoanApplication.class)))
                .thenAnswer(invocation -> {
                    LoanApplication loanToSave = invocation.getArgument(0);
                    assertThat(loanToSave.getLoanStatus()).isEqualTo(LoanStatusCode.PENDING_REVIEW);
                    return Mono.just(loanToSave);
                });

        StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
                .expectNextCount(1)
                .verifyComplete();

        verify(loanApplicationRepositoryGateway).saveLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería propagar error cuando el usuario no existe")
    void shouldPropagateErrorWhenUserDoesNotExist() {
        String errorMessage = "Usuario no encontrado";
        when(loanRestClientGateway.findUserByDocumentId(validLoanApplication.getDocumentId()))
                .thenReturn(Mono.error(new RuntimeException(errorMessage)));

        StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
                .expectError(RuntimeException.class)
                .verify();

        verify(loanApplicationRepositoryGateway, never()).saveLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería propagar error cuando la validación falla")
    void shouldPropagateErrorWhenValidationFails() {
        LoanApplication invalidLoan = validLoanApplication.toBuilder()
                .documentId("") // Documento vacío para fallar validación
                .build();

        when(loanRestClientGateway.findUserByDocumentId(invalidLoan.getDocumentId()))
                .thenReturn(Mono.just(validUser));

        StepVerifier.create(loanApplicationUseCase.saveUser(invalidLoan))
                .expectError()
                .verify();

        verify(loanApplicationRepositoryGateway, never()).saveLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería propagar error cuando el repositorio falla al guardar")
    void shouldPropagateErrorWhenRepositoryFailsToSave() {
        String errorMessage = "Error al guardar en base de datos";
        when(loanRestClientGateway.findUserByDocumentId(validLoanApplication.getDocumentId()))
                .thenReturn(Mono.just(validUser));
        when(loanApplicationRepositoryGateway.saveLoanApplication(any(LoanApplication.class)))
                .thenReturn(Mono.error(new RuntimeException(errorMessage)));

        StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
                .expectError(RuntimeException.class)
                .verify();

        verify(loanRestClientGateway).findUserByDocumentId(validLoanApplication.getDocumentId());
        verify(loanApplicationRepositoryGateway).saveLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería llamar al validador con la solicitud de préstamo correcta")
    void shouldCallValidatorWithCorrectLoanApplication() {
        when(loanRestClientGateway.findUserByDocumentId(validLoanApplication.getDocumentId()))
                .thenReturn(Mono.just(validUser));
        when(loanApplicationRepositoryGateway.saveLoanApplication(any(LoanApplication.class)))
                .thenReturn(Mono.just(validLoanApplication));

        try (MockedStatic<LoanApplicationValidator> mockedValidator = mockStatic(LoanApplicationValidator.class)) {
            StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
                    .expectNext(validLoanApplication)
                    .verifyComplete();

            mockedValidator.verify(() -> LoanApplicationValidator.validate(validLoanApplication));
        }
    }

    @Test
    @DisplayName("Debería manejar correctamente el flujo reactivo cuando el cliente REST retorna un Mono vacío")
    void shouldHandleReactiveFlowWhenRestClientReturnsEmptyMono() {
        when(loanRestClientGateway.findUserByDocumentId(validLoanApplication.getDocumentId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
                .expectComplete()
                .verify();

        verify(loanApplicationRepositoryGateway, never()).saveLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería preservar el ID de la solicitud de préstamo durante el proceso")
    void shouldPreserveLoanApplicationIdDuringProcess() {
        UUID originalId = validLoanApplication.getLoanApplicationId();
        when(loanRestClientGateway.findUserByDocumentId(validLoanApplication.getDocumentId()))
                .thenReturn(Mono.just(validUser));
        when(loanApplicationRepositoryGateway.saveLoanApplication(any(LoanApplication.class)))
                .thenAnswer(invocation -> {
                    LoanApplication loanToSave = invocation.getArgument(0);
                    assertThat(loanToSave.getLoanApplicationId()).isEqualTo(originalId);
                    return Mono.just(loanToSave);
                });

        StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
                .expectNextCount(1)
                .verifyComplete();

        verify(loanApplicationRepositoryGateway).saveLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería manejar correctamente el caso cuando el documento ID es null")
    void shouldHandleNullDocumentId() {
        LoanApplication loanWithNullDocument = validLoanApplication.toBuilder()
                .documentId(null)
                .build();

        when(loanRestClientGateway.findUserByDocumentId(null))
                .thenReturn(Mono.just(validUser));

        StepVerifier.create(loanApplicationUseCase.saveUser(loanWithNullDocument))
                .expectError()
                .verify();

        verify(loanApplicationRepositoryGateway, never()).saveLoanApplication(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Debería completar el flujo reactivo correctamente sin errores")
    void shouldCompleteReactiveFlowSuccessfully() {
        when(loanRestClientGateway.findUserByDocumentId(validLoanApplication.getDocumentId()))
                .thenReturn(Mono.just(validUser));
        when(loanApplicationRepositoryGateway.saveLoanApplication(any(LoanApplication.class)))
                .thenReturn(Mono.just(validLoanApplication));

        StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
                .expectNextMatches(loan -> 
                    loan.getLoanStatus().equals(LoanStatusCode.PENDING_REVIEW) &&
                    loan.getDocumentId().equals(validLoanApplication.getDocumentId()) &&
                    loan.getAmount().equals(validLoanApplication.getAmount())
                )
                .verifyComplete();
    }
}
