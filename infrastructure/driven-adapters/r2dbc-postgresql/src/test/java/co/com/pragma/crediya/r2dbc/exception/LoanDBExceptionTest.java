package co.com.pragma.crediya.r2dbc.exception;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.exception.LoanTypeNotExistsException;
import io.r2dbc.spi.R2dbcException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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

    @ParameterizedTest(name = "[{index}] sqlState={0}, msg=''{1}'' → LoanTypeNotExistsException")
    @CsvSource({
            "23503, violates foreign key constraint fk_solicitudes_tipo_prestamo",
            "00000, violates foreign key constraint fk_solicitudes_tipo_prestamo",
            "00000, violates FOREIGN KEY constraint fk_solicitudes_tipo_prestamo"
    })
    @DisplayName("Mapea correctamente violación de clave foránea por sqlState y/o palabra clave en el mensaje")
    void shouldMapForeignKeyViolation(String sqlState, String errorMessage) {

        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn(sqlState);
        when(r2dbcException.getMessage()).thenReturn(errorMessage);

        DataIntegrityViolationException dive =
                new DataIntegrityViolationException("constraint violation", r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(dive, loanApplication);

        assertThat(result)
                .isInstanceOf(LoanTypeNotExistsException.class)
                .hasMessage("No existe el tipo de prestamo: HIPOTECARIO");
    }

    @ParameterizedTest(name = "[{index}] sqlState={0} | message=''{1}'' → retorna la excepción original")
    @MethodSource("nonForeignKeyScenarios")
    @DisplayName("Debe retornar la excepción original cuando NO es violación de clave foránea")
    void shouldReturnOriginalExceptionWhenNotForeignKeyViolation(String sqlState, String errorMessage) {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn(sqlState);
        when(r2dbcException.getMessage()).thenReturn(errorMessage);

        DataIntegrityViolationException original =
                new DataIntegrityViolationException("constraint violation", r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(original, loanApplication);

        assertThat(result).isSameAs(original);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> nonForeignKeyScenarios() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("23505", "duplicate key value violates unique constraint"),
                org.junit.jupiter.params.provider.Arguments.of("22001", "value too long for type character varying(10)"),
                org.junit.jupiter.params.provider.Arguments.of("23514", "new row for relation violates check constraint"),
                org.junit.jupiter.params.provider.Arguments.of("23502", "null value in column \"name\" violates not-null constraint")
        );
    }

    @Test
    @DisplayName("Debe retornar la excepción original cuando no hay R2dbcException")
    void shouldReturnOriginalExceptionWhenNoR2dbcException() {
        RuntimeException rootCause = new RuntimeException("Database connection failed");
        when(dataIntegrityViolationException.getCause()).thenReturn(rootCause);

        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando R2dbcException no tiene mensaje")
    void shouldHandleCorrectlyWhenR2dbcExceptionHasNoMessage() {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn(null);
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);
        when(dataIntegrityViolationException.getMessage()).thenReturn("Data integrity violation");

        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @ParameterizedTest(name = "[{index}] sqlState=''{0}'' (null/vacío/espacios) → LoanTypeNotExistsException")
    @MethodSource("nullOrBlankSqlStates")
    @DisplayName("Debe manejar correctamente cuando R2dbcException no tiene sqlState (null/blank)")
    void shouldHandleCorrectlyWhenR2dbcExceptionHasNoSqlState(String sqlState) {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn(sqlState);
        when(r2dbcException.getMessage())
                .thenReturn("violates foreign key constraint fk_solicitudes_tipo_prestamo");

        DataIntegrityViolationException dive =
                new DataIntegrityViolationException("constraint violation", r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(dive, loanApplication);

        assertThat(result)
                .isInstanceOf(LoanTypeNotExistsException.class)
                .hasMessage("No existe el tipo de prestamo: HIPOTECARIO");
    }

    static Stream<String> nullOrBlankSqlStates() {
        return Stream.of(null, "", "   ");
    }

    @ParameterizedTest(name = "[{index}] constraint=''{0}'' → devuelve la excepción original")
    @MethodSource("unknownForeignKeyConstraints")
    @DisplayName("Retorna la excepción original cuando no hay mapeo específico para la FK")
    void shouldReturnOriginalExceptionWhenNoSpecificMappingForForeignKeyViolation(String unknownConstraintName) {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503"); // FK violation
        when(r2dbcException.getMessage())
                .thenReturn("violates foreign key constraint " + unknownConstraintName);

        DataIntegrityViolationException original =
                new DataIntegrityViolationException("constraint violation", r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(original, loanApplication);

        assertThat(result).isSameAs(original);
    }

    static Stream<String> unknownForeignKeyConstraints() {
        return Stream.of(
                "fk_other_table",
                "fk_users_roles",
                "fk_payments_orders",
                "fk_inventario_producto"
        );
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje es null")
    void shouldHandleCorrectlyWhenMessageIsNull() {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn(null);
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);
        when(dataIntegrityViolationException.getMessage()).thenReturn(null);

        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje está vacío")
    void shouldHandleCorrectlyWhenMessageIsEmpty() {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn("");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene solo espacios en blanco")
    void shouldHandleCorrectlyWhenMessageContainsOnlyWhitespace() {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("23503");
        when(r2dbcException.getMessage()).thenReturn("   ");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        assertNotNull(result);
        assertEquals(dataIntegrityViolationException, result);
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene 'FOREIGN KEY' en mayúsculas")
    void shouldHandleCorrectlyWhenMessageContainsForeignKeyInUppercase() {

        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn("VIOLATES FOREIGN KEY CONSTRAINT fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }

    @ParameterizedTest(name = "[{index}] msg=''{0}'' → LoanTypeNotExistsException")
    @ValueSource(strings = {
            "violates FOREIGN KEY constraint",
            "violates foreign key constraint"
    })
    @DisplayName("Debe mapear correctamente por palabra clave en el mensaje")
    void shouldMapForeignKeyViolationByKeywordInMessage(String msgPrefix) {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn(msgPrefix + " fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(
                dataIntegrityViolationException, loanApplication
        );

        assertThat(result)
                .isInstanceOf(LoanTypeNotExistsException.class)
                .hasMessage("No existe el tipo de prestamo: HIPOTECARIO");
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando el mensaje contiene 'Foreign Key' con capitalización mixta")
    void shouldHandleCorrectlyWhenMessageContainsForeignKeyInMixedCase() {
        R2dbcException r2dbcException = mock(R2dbcException.class);
        when(r2dbcException.getSqlState()).thenReturn("00000");
        when(r2dbcException.getMessage()).thenReturn("Violates Foreign Key Constraint fk_solicitudes_tipo_prestamo");
        when(dataIntegrityViolationException.getCause()).thenReturn(r2dbcException);

        RuntimeException result = LoanDBException.valideDBException(dataIntegrityViolationException, loanApplication);

        assertNotNull(result);
        assertTrue(result instanceof LoanTypeNotExistsException);
        assertEquals("No existe el tipo de prestamo: HIPOTECARIO", ((LoanTypeNotExistsException) result).getMessage());
    }
}
