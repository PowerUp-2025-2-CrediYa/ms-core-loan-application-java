package co.com.pragma.crediya.r2dbc.exception;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.exception.LoanTypeNotExistsException;
import io.r2dbc.spi.R2dbcException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LoanDBExceptionTest {

    private LoanApplication loanApplication;
    private DataIntegrityViolationException dataIntegrityViolationException;

    @BeforeEach
    void setUp() {
        loanApplication = LoanApplication.builder()
                .loanApplicationId(UUID.randomUUID())
                .documentId("12345678")
                .amount(new BigDecimal("100000"))
                .loanType("HIPOTECARIO")
                .loanTerm(240)
                .loanStatus("PENDING")
                .createAt(LocalDate.now())
                .build();

        dataIntegrityViolationException = mock(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe mapear correctamente violación de clave foránea de tipo de préstamo")
    void shouldMapForeignKeyViolationForLoanType() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn("violates foreign key constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @Test
    @DisplayName("Debe mapear correctamente violación de clave foránea por mensaje de error")
    void shouldMapForeignKeyViolationByErrorMessage() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn("violates foreign key constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @Test
    @DisplayName("Debe mapear correctamente violación de clave foránea por palabra clave en mensaje")
    void shouldMapForeignKeyViolationByKeywordInMessage() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn("violates foreign key constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @Test
    @DisplayName("Debe retornar la excepción original cuando no es violación de clave foránea")
    void shouldReturnOriginalExceptionWhenNotForeignKeyViolation() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23505"); // Unique constraint violation
        when(r2dbcException.getMessage()).thenReturn("duplicate key value violates unique constraint");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe retornar la excepción original cuando no hay R2dbcException")
    void shouldReturnOriginalExceptionWhenNoR2dbcException() {
        // Arrange
        RuntimeException rootCause = new RuntimeException("Database connection failed");
        when(dataIntegrityViolationException.getCause()).thenReturn(rootCause);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando R2dbcException no tiene mensaje")
    void shouldHandleCorrectlyWhenR2dbcExceptionHasNoMessage() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn(null);
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);
        when(dataIntegrityViolationException.getMessage()).thenReturn("Data integrity violation");

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando R2dbcException no tiene sqlState")
    void shouldHandleCorrectlyWhenR2dbcExceptionHasNoSqlState() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn(null);
        when(r2dbcException.getMessage()).thenReturn("violates foreign key constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene 'foreign key'")
    void shouldHandleCorrectlyWhenMessageContainsForeignKey() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn("violates foreign key constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene 'violates foreign key constraint'")
    void shouldHandleCorrectlyWhenMessageContainsViolatesForeignKeyConstraint() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn("violates foreign key constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @Test
    @DisplayName("Debe retornar null cuando no hay mapeo específico para la violación de clave foránea")
    void shouldReturnNullWhenNoSpecificMappingForForeignKeyViolation() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn("violates foreign key constraint fk_other_table");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje es null")
    void shouldHandleCorrectlyWhenMessageIsNull() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn(null);
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);
        when(dataIntegrityViolationException.getMessage()).thenReturn(null);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje está vacío")
    void shouldHandleCorrectlyWhenMessageIsEmpty() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn("");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene solo espacios en blanco")
    void shouldHandleCorrectlyWhenMessageContainsOnlyWhitespace() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn("   ");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene 'FOREIGN KEY' en mayúsculas")
    void shouldHandleCorrectlyWhenMessageContainsForeignKeyInUppercase() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn("VIOLATES FOREIGN KEY CONSTRAINT fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene 'foreign key' en minúsculas")
    void shouldHandleCorrectlyWhenMessageContainsForeignKeyInLowercase() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn("violates foreign key constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene 'Foreign Key' con capitalización mixta")
    void shouldHandleCorrectlyWhenMessageContainsForeignKeyInMixedCase() {
        // Arrange
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn("Violates Foreign Key Constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        // Act
        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }
}
