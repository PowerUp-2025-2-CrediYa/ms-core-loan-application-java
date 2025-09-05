package co.com.pragma.crediya.model.loanapplication.helper;

import co.com.pragma.crediya.model.loanapplication.exception.InvalidAmountRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AmountValidatorTest {

    @Test
    @DisplayName("Debe lanzar InvalidAmountRangeException cuando el monto es nulo")
    void shouldThrowInvalidAmountRangeExceptionWhenAmountIsNull() {
        // Arrange
        BigDecimal nullAmount = null;

        // Act & Assert
        assertThrows(
                InvalidAmountRangeException.class,
                () -> AmountValidator.validate(nullAmount)
        );
    }

    @Test
    @DisplayName("Debe lanzar InvalidAmountRangeException cuando el monto es cero")
    void shouldThrowInvalidAmountRangeExceptionWhenAmountIsZero() {
        // Arrange
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // Act & Assert
        assertThrows(
                InvalidAmountRangeException.class,
                () -> AmountValidator.validate(zeroAmount)
        );
    }

    @Test
    @DisplayName("Debe lanzar InvalidAmountRangeException cuando el monto es negativo")
    void shouldThrowInvalidAmountRangeExceptionWhenAmountIsNegative() {
        // Arrange
        BigDecimal negativeAmount = new BigDecimal("-100.50");

        // Act & Assert
        assertThrows(
                InvalidAmountRangeException.class,
                () -> AmountValidator.validate(negativeAmount)
        );
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el monto es positivo")
    void shouldValidateSuccessfullyWhenAmountIsPositive() {
        // Arrange
        BigDecimal positiveAmount = new BigDecimal("100.50");

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(positiveAmount));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el monto es un entero positivo")
    void shouldValidateSuccessfullyWhenAmountIsPositiveInteger() {
        // Arrange
        BigDecimal positiveIntegerAmount = new BigDecimal("1000");

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(positiveIntegerAmount));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el monto es un decimal positivo")
    void shouldValidateSuccessfullyWhenAmountIsPositiveDecimal() {
        // Arrange
        BigDecimal positiveDecimalAmount = new BigDecimal("999.99");

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(positiveDecimalAmount));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el monto es un número muy grande")
    void shouldValidateSuccessfullyWhenAmountIsVeryLarge() {
        // Arrange
        BigDecimal veryLargeAmount = new BigDecimal("999999999.99");

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(veryLargeAmount));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el monto es exactamente el mínimo valor positivo")
    void shouldValidateSuccessfullyWhenAmountIsMinimumPositiveValue() {
        // Arrange
        BigDecimal minimumPositiveAmount = new BigDecimal("0.01");

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(minimumPositiveAmount));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "1000.50", "999999.99", "0.01", "0.1", "1.0"})
    @DisplayName("Debe validar correctamente montos positivos válidos")
    void shouldValidateSuccessfullyForValidPositiveAmounts(String amountString) {
        // Arrange
        BigDecimal amount = new BigDecimal(amountString);

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(amount));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-100.50", "-0.01", "-999999.99"})
    @DisplayName("Debe lanzar InvalidAmountRangeException para montos inválidos")
    void shouldThrowInvalidAmountRangeExceptionForInvalidAmounts(String amountString) {
        // Arrange
        BigDecimal amount = new BigDecimal(amountString);

        // Act & Assert
        assertThrows(InvalidAmountRangeException.class, () -> AmountValidator.validate(amount));
    }

    @Test
    @DisplayName("Debe manejar correctamente BigDecimal con diferentes escalas")
    void shouldHandleBigDecimalWithDifferentScales() {
        // Arrange
        BigDecimal amountWithScale = new BigDecimal("100.0000");

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(amountWithScale));
    }

    @Test
    @DisplayName("Debe manejar correctamente BigDecimal con notación científica")
    void shouldHandleBigDecimalWithScientificNotation() {
        // Arrange
        BigDecimal amountWithScientificNotation = new BigDecimal("1E+2"); // 100

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(amountWithScientificNotation));
    }

    @Test
    @DisplayName("Debe lanzar InvalidAmountRangeException para BigDecimal con notación científica negativa")
    void shouldThrowInvalidAmountRangeExceptionForNegativeScientificNotation() {
        // Arrange
        BigDecimal negativeScientificAmount = new BigDecimal("-1E+2"); // -100

        // Act & Assert
        assertThrows(InvalidAmountRangeException.class, () -> AmountValidator.validate(negativeScientificAmount));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el monto es exactamente 1")
    void shouldValidateSuccessfullyWhenAmountIsExactlyOne() {
        // Arrange
        BigDecimal oneAmount = BigDecimal.ONE;

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(oneAmount));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el monto es exactamente 10")
    void shouldValidateSuccessfullyWhenAmountIsExactlyTen() {
        // Arrange
        BigDecimal tenAmount = BigDecimal.TEN;

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(tenAmount));
    }

    @Test
    @DisplayName("Debe lanzar InvalidAmountRangeException cuando el monto es cero con diferentes representaciones")
    void shouldThrowInvalidAmountRangeExceptionForZeroInDifferentRepresentations() {
        // Arrange
        BigDecimal zero1 = new BigDecimal("0.0");
        BigDecimal zero2 = new BigDecimal("0.00");
        BigDecimal zero3 = new BigDecimal("0.000");

        // Act & Assert
        assertThrows(InvalidAmountRangeException.class, () -> AmountValidator.validate(zero1));
        assertThrows(InvalidAmountRangeException.class, () -> AmountValidator.validate(zero2));
        assertThrows(InvalidAmountRangeException.class, () -> AmountValidator.validate(zero3));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando el monto es un número con muchos decimales")
    void shouldValidateSuccessfullyWhenAmountHasManyDecimals() {
        // Arrange
        BigDecimal amountWithManyDecimals = new BigDecimal("123.456789");

        // Act & Assert
        assertDoesNotThrow(() -> AmountValidator.validate(amountWithManyDecimals));
    }

    @Test
    @DisplayName("Debe lanzar InvalidAmountRangeException cuando el monto es negativo con muchos decimales")
    void shouldThrowInvalidAmountRangeExceptionWhenAmountIsNegativeWithManyDecimals() {
        // Arrange
        BigDecimal negativeAmountWithManyDecimals = new BigDecimal("-123.456789");

        // Act & Assert
        assertThrows(InvalidAmountRangeException.class, () -> AmountValidator.validate(negativeAmountWithManyDecimals));
    }
}