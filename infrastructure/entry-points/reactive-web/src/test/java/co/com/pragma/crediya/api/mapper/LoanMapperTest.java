package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.model.request.LoanApplicationRequest;
import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LoanMapperTest {

    private LoanApplicationRequest loanApplicationRequest;

    @BeforeEach
    void setUp() {
        loanApplicationRequest = LoanApplicationRequest.builder()
                .documentId("12345678")
                .amount(new BigDecimal("100000"))
                .loanType("HIPOTECARIO")
                .loanTerm(240)
                .build();
    }

    @Test
    @DisplayName("Debe mapear correctamente un LoanApplicationRequest válido a LoanApplication")
    void shouldMapValidLoanApplicationRequestToLoanApplication() {
        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
        assertNull(result.getLoanApplicationId());
        assertNull(result.getLoanStatus());
        assertNull(result.getCreateAt());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el documento de identidad es null")
    void shouldMapCorrectlyWhenDocumentIdIsNull() {
        // Arrange
        loanApplicationRequest.setDocumentId(null);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertNull(result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el documento de identidad está vacío")
    void shouldMapCorrectlyWhenDocumentIdIsEmpty() {
        // Arrange
        loanApplicationRequest.setDocumentId("");

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es null")
    void shouldMapCorrectlyWhenAmountIsNull() {
        // Arrange
        loanApplicationRequest.setAmount(null);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertNull(result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el tipo de préstamo es null")
    void shouldMapCorrectlyWhenLoanTypeIsNull() {
        // Arrange
        loanApplicationRequest.setLoanType(null);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertNull(result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el tipo de préstamo está vacío")
    void shouldMapCorrectlyWhenLoanTypeIsEmpty() {
        // Arrange
        loanApplicationRequest.setLoanType("");

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el plazo del préstamo es null")
    void shouldMapCorrectlyWhenLoanTermIsNull() {
        // Arrange
        loanApplicationRequest.setLoanTerm(null);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertNull(result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando todos los campos son null")
    void shouldMapCorrectlyWhenAllFieldsAreNull() {
        // Arrange
        loanApplicationRequest.setDocumentId(null);
        loanApplicationRequest.setAmount(null);
        loanApplicationRequest.setLoanType(null);
        loanApplicationRequest.setLoanTerm(null);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertNull(result.getDocumentId());
        assertNull(result.getAmount());
        assertNull(result.getLoanType());
        assertNull(result.getLoanTerm());
        assertNull(result.getLoanApplicationId());
        assertNull(result.getLoanStatus());
        assertNull(result.getCreateAt());
    }

    @Test
    @DisplayName("Debe mapear correctamente un préstamo hipotecario")
    void shouldMapCorrectlyMortgageLoan() {
        // Arrange
        loanApplicationRequest.setLoanType("HIPOTECARIO");
        loanApplicationRequest.setAmount(new BigDecimal("500000000"));
        loanApplicationRequest.setLoanTerm(300);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("500000000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(300, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente un préstamo personal")
    void shouldMapCorrectlyPersonalLoan() {
        // Arrange
        loanApplicationRequest.setLoanType("PERSONAL");
        loanApplicationRequest.setAmount(new BigDecimal("5000000"));
        loanApplicationRequest.setLoanTerm(36);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("5000000"), result.getAmount());
        assertEquals("PERSONAL", result.getLoanType());
        assertEquals(36, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente un préstamo de vehículo")
    void shouldMapCorrectlyVehicleLoan() {
        // Arrange
        loanApplicationRequest.setLoanType("VEHICULO");
        loanApplicationRequest.setAmount(new BigDecimal("30000000"));
        loanApplicationRequest.setLoanTerm(60);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("30000000"), result.getAmount());
        assertEquals("VEHICULO", result.getLoanType());
        assertEquals(60, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente un préstamo estudiantil")
    void shouldMapCorrectlyStudentLoan() {
        // Arrange
        loanApplicationRequest.setLoanType("ESTUDIANTIL");
        loanApplicationRequest.setAmount(new BigDecimal("15000000"));
        loanApplicationRequest.setLoanTerm(120);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("15000000"), result.getAmount());
        assertEquals("ESTUDIANTIL", result.getLoanType());
        assertEquals(120, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente un préstamo comercial")
    void shouldMapCorrectlyCommercialLoan() {
        // Arrange
        loanApplicationRequest.setLoanType("COMERCIAL");
        loanApplicationRequest.setAmount(new BigDecimal("100000000"));
        loanApplicationRequest.setLoanTerm(240);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000000"), result.getAmount());
        assertEquals("COMERCIAL", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es cero")
    void shouldMapCorrectlyWhenAmountIsZero() {
        // Arrange
        loanApplicationRequest.setAmount(BigDecimal.ZERO);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(BigDecimal.ZERO, result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es negativo")
    void shouldMapCorrectlyWhenAmountIsNegative() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("-100000"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("-100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el plazo del préstamo es cero")
    void shouldMapCorrectlyWhenLoanTermIsZero() {
        // Arrange
        loanApplicationRequest.setLoanTerm(0);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(0, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el plazo del préstamo es negativo")
    void shouldMapCorrectlyWhenLoanTermIsNegative() {
        // Arrange
        loanApplicationRequest.setLoanTerm(-12);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(-12, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el plazo del préstamo es el valor máximo de Integer")
    void shouldMapCorrectlyWhenLoanTermIsMaxIntegerValue() {
        // Arrange
        loanApplicationRequest.setLoanTerm(Integer.MAX_VALUE);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(Integer.MAX_VALUE, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el plazo del préstamo es el valor mínimo de Integer")
    void shouldMapCorrectlyWhenLoanTermIsMinIntegerValue() {
        // Arrange
        loanApplicationRequest.setLoanTerm(Integer.MIN_VALUE);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(Integer.MIN_VALUE, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un valor muy grande")
    void shouldMapCorrectlyWhenAmountIsVeryLarge() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("999999999999999999.99"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("999999999999999999.99"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un valor muy pequeño")
    void shouldMapCorrectlyWhenAmountIsVerySmall() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("0.01"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("0.01"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HIPOTECARIO", "PERSONAL", "VEHICULO", "ESTUDIANTIL", "COMERCIAL", "MICROCREDITO"})
    @DisplayName("Debe mapear correctamente diferentes tipos de préstamo")
    void shouldMapCorrectlyDifferentLoanTypes(String loanType) {
        // Arrange
        loanApplicationRequest.setLoanType(loanType);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals(loanType, result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6, 12, 24, 36, 48, 60, 120, 240, 360})
    @DisplayName("Debe mapear correctamente diferentes plazos de préstamo")
    void shouldMapCorrectlyDifferentLoanTerms(int loanTerm) {
        // Arrange
        loanApplicationRequest.setLoanTerm(loanTerm);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(loanTerm, result.getLoanTerm());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890", "9876543210", "1111111111", "9999999999"})
    @DisplayName("Debe mapear correctamente diferentes documentos de identidad")
    void shouldMapCorrectlyDifferentDocumentIds(String documentId) {
        // Arrange
        loanApplicationRequest.setDocumentId(documentId);

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals(documentId, result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el documento de identidad contiene caracteres especiales")
    void shouldMapCorrectlyWhenDocumentIdContainsSpecialCharacters() {
        // Arrange
        loanApplicationRequest.setDocumentId("123-456-789");

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("123-456-789", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el documento de identidad contiene espacios")
    void shouldMapCorrectlyWhenDocumentIdContainsSpaces() {
        // Arrange
        loanApplicationRequest.setDocumentId("123 456 789");

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("123 456 789", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el tipo de préstamo contiene espacios")
    void shouldMapCorrectlyWhenLoanTypeContainsSpaces() {
        // Arrange
        loanApplicationRequest.setLoanType("PRESTAMO PERSONAL");

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("PRESTAMO PERSONAL", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el tipo de préstamo contiene caracteres especiales")
    void shouldMapCorrectlyWhenLoanTypeContainsSpecialCharacters() {
        // Arrange
        loanApplicationRequest.setLoanType("PRESTAMO-COMERCIAL");

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("PRESTAMO-COMERCIAL", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el tipo de préstamo está en minúsculas")
    void shouldMapCorrectlyWhenLoanTypeIsLowerCase() {
        // Arrange
        loanApplicationRequest.setLoanType("hipotecario");

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("hipotecario", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el tipo de préstamo tiene capitalización mixta")
    void shouldMapCorrectlyWhenLoanTypeHasMixedCase() {
        // Arrange
        loanApplicationRequest.setLoanType("Hipotecario");

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("Hipotecario", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto tiene muchos decimales")
    void shouldMapCorrectlyWhenAmountHasManyDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("123456.789012345"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("123456.789012345"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es exactamente cero")
    void shouldMapCorrectlyWhenAmountIsExactlyZero() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("0.00"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("0.00"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número entero")
    void shouldMapCorrectlyWhenAmountIsWholeNumber() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con un solo decimal")
    void shouldMapCorrectlyWhenAmountHasSingleDecimal() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.5"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.5"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con dos decimales")
    void shouldMapCorrectlyWhenAmountHasTwoDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.99"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.99"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con tres decimales")
    void shouldMapCorrectlyWhenAmountHasThreeDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.999"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.999"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con cuatro decimales")
    void shouldMapCorrectlyWhenAmountHasFourDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.9999"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.9999"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con cinco decimales")
    void shouldMapCorrectlyWhenAmountHasFiveDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.99999"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.99999"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con seis decimales")
    void shouldMapCorrectlyWhenAmountHasSixDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.999999"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.999999"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con siete decimales")
    void shouldMapCorrectlyWhenAmountHasSevenDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.9999999"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.9999999"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con ocho decimales")
    void shouldMapCorrectlyWhenAmountHasEightDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.99999999"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.99999999"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con nueve decimales")
    void shouldMapCorrectlyWhenAmountHasNineDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.999999999"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.999999999"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }

    @Test
    @DisplayName("Debe mapear correctamente cuando el monto es un número con diez decimales")
    void shouldMapCorrectlyWhenAmountHasTenDecimals() {
        // Arrange
        loanApplicationRequest.setAmount(new BigDecimal("100000.9999999999"));

        // Act
        LoanApplication result = LoanMapper.toDomain(loanApplicationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getDocumentId());
        assertEquals(new BigDecimal("100000.9999999999"), result.getAmount());
        assertEquals("HIPOTECARIO", result.getLoanType());
        assertEquals(240, result.getLoanTerm());
    }
}
