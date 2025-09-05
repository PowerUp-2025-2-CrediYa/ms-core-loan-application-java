package co.com.pragma.crediya.usecase.loanapplication;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Builder de datos de prueba para LoanApplicationUseCase
 * 
 * Implementa el patrón Builder para crear objetos de prueba de manera fluida
 * y siguiendo el principio de Single Responsibility Principle (SRP)
 */
public  final class LoanApplicationUseCaseTestDataBuilder {

    private LoanApplicationUseCaseTestDataBuilder() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Crea un LoanApplication válido para pruebas
     */
    public static LoanApplication validLoanApplication() {
        return LoanApplication.builder()
                .loanApplicationId(UUID.randomUUID())
                .documentId("12345678")
                .amount(new BigDecimal("1000000"))
                .loanType("PERSONAL")
                .loanTerm(12)
                .createAt(LocalDate.now())
                .build();
    }

    /**
     * Crea un LoanApplication con documento ID específico
     */
    public static LoanApplication loanApplicationWithDocumentId(String documentId) {
        return validLoanApplication().toBuilder()
                .documentId(documentId)
                .build();
    }

    /**
     * Crea un LoanApplication con monto específico
     */
    public static LoanApplication loanApplicationWithAmount(BigDecimal amount) {
        return validLoanApplication().toBuilder()
                .amount(amount)
                .build();
    }

    /**
     * Crea un LoanApplication con tipo de préstamo específico
     */
    public static LoanApplication loanApplicationWithLoanType(String loanType) {
        return validLoanApplication().toBuilder()
                .loanType(loanType)
                .build();
    }

    /**
     * Crea un LoanApplication con plazo específico
     */
    public static LoanApplication loanApplicationWithLoanTerm(Integer loanTerm) {
        return validLoanApplication().toBuilder()
                .loanTerm(loanTerm)
                .build();
    }

    /**
     * Crea un LoanApplication inválido (con documento vacío)
     */
    public static LoanApplication invalidLoanApplicationWithEmptyDocument() {
        return validLoanApplication().toBuilder()
                .documentId("")
                .build();
    }

    /**
     * Crea un LoanApplication inválido (con documento null)
     */
    public static LoanApplication invalidLoanApplicationWithNullDocument() {
        return validLoanApplication().toBuilder()
                .documentId(null)
                .build();
    }

    /**
     * Crea un LoanApplication inválido (con tipo de préstamo vacío)
     */
    public static LoanApplication invalidLoanApplicationWithEmptyLoanType() {
        return validLoanApplication().toBuilder()
                .loanType("")
                .build();
    }

    /**
     * Crea un LoanApplication inválido (con monto null)
     */
    public static LoanApplication invalidLoanApplicationWithNullAmount() {
        return validLoanApplication().toBuilder()
                .amount(null)
                .build();
    }

    /**
     * Crea un LoanApplication inválido (con plazo null)
     */
    public static LoanApplication invalidLoanApplicationWithNullLoanTerm() {
        return validLoanApplication().toBuilder()
                .loanTerm(null)
                .build();
    }

    /**
     * Crea un User válido para pruebas
     */
    public static User validUser() {
        return User.builder()
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

    /**
     * Crea un User con documento ID específico
     */
    public static User userWithDocumentId(String documentId) {
        return validUser().toBuilder()
                .documentId(documentId)
                .build();
    }

    /**
     * Crea un User con salario base específico
     */
    public static User userWithBaseSalary(Double baseSalary) {
        return validUser().toBuilder()
                .baseSalary(baseSalary)
                .build();
    }
}

