package co.com.pragma.crediya.model.loanapplication.helper;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.exception.InvalidAmountRangeException;
import co.com.pragma.crediya.model.loanapplication.exception.InvalidLoanException;
import co.com.pragma.crediya.model.loanapplication.exception.InvalidLoanTermException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationValidatorTest {

    @Mock
    private AmountValidator amountValidator;

    @Mock
    private LoanTermvalidator loanTermvalidator;

    private LoanApplication baseLoan;

    @BeforeEach
    void setUp() {
        baseLoan = LoanApplication.builder()
                .documentId("123456789")
                .loanType("PERSONAL")
                .loanTerm(12)
                .amount(BigDecimal.valueOf(5000000))
                .build();
    }

    @Test
    void shouldThrowExceptionWhenDocumentIdIsNull() {
        baseLoan.setDocumentId(null);

        InvalidLoanException ex = assertThrows(InvalidLoanException.class, () ->
                LoanApplicationValidator.validate(baseLoan)
        );

        assertEquals("El numero de identificación no puede estar vacío", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoanTypeIsEmpty() {
        baseLoan.setLoanType("   ");

        InvalidLoanException ex = assertThrows(InvalidLoanException.class, () ->
                LoanApplicationValidator.validate(baseLoan)
        );

        assertEquals("El tipo de prestamo no puede estar vacío", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoanTermIsNull() {
        baseLoan.setLoanTerm(0);

        InvalidLoanTermException ex = assertThrows(InvalidLoanTermException.class, () ->
                LoanApplicationValidator.validate(baseLoan)
        );

        assertEquals("Plazo Invalido: 0", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        baseLoan.setAmount(BigDecimal.ZERO);

        InvalidAmountRangeException ex = assertThrows(InvalidAmountRangeException.class, () ->
                LoanApplicationValidator.validate(baseLoan)
        );

        assertEquals("Monto fuera del rango: 0", ex.getMessage());
    }

    @Test
    void shouldValidateSuccessfullyWhenAllFieldsAreValid() {
        assertDoesNotThrow(() -> LoanApplicationValidator.validate(baseLoan));
    }

}
