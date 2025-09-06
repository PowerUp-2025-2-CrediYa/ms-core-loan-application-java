package co.com.pragma.crediya.model.loanapplication.helper;

import co.com.pragma.crediya.model.loanapplication.exception.InvalidAmountRangeException;
import co.com.pragma.crediya.model.loanapplication.exception.InvalidLoanTermException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LoanTermvalidatorTest {

    @Test
    @DisplayName("Debe lanzar InvalidAmountRangeException cuando el plazo del préstamo es nulo")
    void shouldThrowInvalidAmountRangeExceptionWhenLoanTermIsNull() {
        Integer nullLoanTerm = null;

        InvalidAmountRangeException exception = assertThrows(
                InvalidAmountRangeException.class,
                () -> LoanTermvalidator.validate(nullLoanTerm)
        );

        assertEquals("Monto fuera del rango: El Monto no puede ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar InvalidLoanTermException cuando el plazo del préstamo es cero")
    void shouldThrowInvalidLoanTermExceptionWhenLoanTermIsZero() {
        Integer zeroLoanTerm = 0;

        InvalidLoanTermException exception = assertThrows(
                InvalidLoanTermException.class,
                () -> LoanTermvalidator.validate(zeroLoanTerm)
        );

        assertEquals("Plazo Invalido: 0", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar InvalidLoanTermException cuando el plazo del préstamo es negativo")
    void shouldThrowInvalidLoanTermExceptionWhenLoanTermIsNegative() {
        Integer negativeLoanTerm = -5;

        InvalidLoanTermException exception = assertThrows(
                InvalidLoanTermException.class,
                () -> LoanTermvalidator.validate(negativeLoanTerm)
        );

        assertEquals("Plazo Invalido: -5", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar InvalidLoanTermException cuando el plazo del préstamo es muy negativo")
    void shouldThrowInvalidLoanTermExceptionWhenLoanTermIsVeryNegative() {
        Integer veryNegativeLoanTerm = -100;

        InvalidLoanTermException exception = assertThrows(
                InvalidLoanTermException.class,
                () -> LoanTermvalidator.validate(veryNegativeLoanTerm)
        );

        assertEquals("Plazo Invalido: -100", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es positivo")
    void shouldValidateSuccessfullyWhenLoanTermIsPositive() {
        Integer positiveLoanTerm = 12;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(positiveLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es 1")
    void shouldValidateSuccessfullyWhenLoanTermIsOne() {
        Integer oneLoanTerm = 1;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(oneLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor típico")
    void shouldValidateSuccessfullyWhenLoanTermIsTypicalValue() {
        Integer typicalLoanTerm = 24;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(typicalLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor alto")
    void shouldValidateSuccessfullyWhenLoanTermIsHighValue() {
        Integer highLoanTerm = 360;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(highLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es el valor máximo de Integer")
    void shouldValidateSuccessfullyWhenLoanTermIsMaxIntegerValue() {
        Integer maxLoanTerm = Integer.MAX_VALUE;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(maxLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor mínimo positivo")
    void shouldValidateSuccessfullyWhenLoanTermIsMinimumPositiveValue() {
        Integer minimumPositiveLoanTerm = 1;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(minimumPositiveLoanTerm));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6, 12, 24, 36, 48, 60, 120, 240, 360})
    @DisplayName("Debe validar correctamente plazos de préstamo válidos")
    void shouldValidateSuccessfullyForValidLoanTerms(int loanTerm) {
        assertDoesNotThrow(() -> LoanTermvalidator.validate(loanTerm));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -6, -12, -24, -36, -48, -60, -120, -240, -360})
    @DisplayName("Debe lanzar InvalidLoanTermException para plazos de préstamo inválidos")
    void shouldThrowInvalidLoanTermExceptionForInvalidLoanTerms(int loanTerm) {
        assertThrows(InvalidLoanTermException.class, () -> LoanTermvalidator.validate(loanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor intermedio")
    void shouldValidateSuccessfullyWhenLoanTermIsIntermediateValue() {
        Integer intermediateLoanTerm = 18;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(intermediateLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor de mediano plazo")
    void shouldValidateSuccessfullyWhenLoanTermIsMediumTerm() {
        Integer mediumTermLoanTerm = 60;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(mediumTermLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor de largo plazo")
    void shouldValidateSuccessfullyWhenLoanTermIsLongTerm() {
        Integer longTermLoanTerm = 180;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(longTermLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor muy largo")
    void shouldValidateSuccessfullyWhenLoanTermIsVeryLongTerm() {
        Integer veryLongTermLoanTerm = 480;

        assertDoesNotThrow(() -> LoanTermvalidator.validate(veryLongTermLoanTerm));
    }

    @Test
    @DisplayName("Debe lanzar InvalidLoanTermException cuando el plazo del préstamo es Integer.MIN_VALUE")
    void shouldThrowInvalidLoanTermExceptionWhenLoanTermIsMinIntegerValue() {
        Integer minLoanTerm = Integer.MIN_VALUE;

        InvalidLoanTermException exception = assertThrows(
                InvalidLoanTermException.class,
                () -> LoanTermvalidator.validate(minLoanTerm)
        );

        assertEquals("Plazo Invalido: ".concat(String.valueOf(minLoanTerm)), exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor común de hipoteca")
    void shouldValidateSuccessfullyWhenLoanTermIsCommonMortgageValue() {
        Integer mortgageLoanTerm = 300; // 25 años en meses

        assertDoesNotThrow(() -> LoanTermvalidator.validate(mortgageLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor común de préstamo personal")
    void shouldValidateSuccessfullyWhenLoanTermIsCommonPersonalLoanValue() {
        Integer personalLoanTerm = 36; // 3 años en meses

        assertDoesNotThrow(() -> LoanTermvalidator.validate(personalLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor común de préstamo de vehículo")
    void shouldValidateSuccessfullyWhenLoanTermIsCommonVehicleLoanValue() {

        Integer vehicleLoanTerm = 60; // 5 años en meses

        assertDoesNotThrow(() -> LoanTermvalidator.validate(vehicleLoanTerm));
    }

    @Test
    @DisplayName("Debe lanzar InvalidLoanTermException cuando el plazo del préstamo es -1")
    void shouldThrowInvalidLoanTermExceptionWhenLoanTermIsNegativeOne() {
        Integer negativeOneLoanTerm = -1;

        InvalidLoanTermException exception = assertThrows(
                InvalidLoanTermException.class,
                () -> LoanTermvalidator.validate(negativeOneLoanTerm)
        );

        assertEquals("Plazo Invalido: ".concat(String.valueOf(negativeOneLoanTerm)), exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor de préstamo estudiantil")
    void shouldValidateSuccessfullyWhenLoanTermIsStudentLoanValue() {
        Integer studentLoanTerm = 120; // 10 años en meses

        assertDoesNotThrow(() -> LoanTermvalidator.validate(studentLoanTerm));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el plazo del préstamo es un valor de préstamo comercial")
    void shouldValidateSuccessfullyWhenLoanTermIsCommercialLoanValue() {
        Integer commercialLoanTerm = 240; // 20 años en meses

        assertDoesNotThrow(() -> LoanTermvalidator.validate(commercialLoanTerm));
    }
}

