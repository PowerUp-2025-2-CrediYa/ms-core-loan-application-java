package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.model.request.LoanApplicationRequest;
import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class LoanMapperTest {

    private static final String DEFAULT_DOCUMENT_ID = "12345678";
    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("100000");
    private static final String DEFAULT_LOAN_TYPE = "HIPOTECARIO";
    private static final Integer DEFAULT_LOAN_TERM = 240;

    @Test
    @DisplayName("Debe mapear un LoanApplicationRequest válido a LoanApplication")
    void shouldMapValidRequestToDomainObject() {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .documentId(DEFAULT_DOCUMENT_ID)
                .amount(DEFAULT_AMOUNT)
                .loanType(DEFAULT_LOAN_TYPE)
                .loanTerm(DEFAULT_LOAN_TERM)
                .build();

        LoanApplication result = LoanMapper.toDomain(request);

        assertThat(result)
                .isNotNull()
                .extracting(
                        LoanApplication::getDocumentId,
                        LoanApplication::getAmount,
                        LoanApplication::getLoanType,
                        LoanApplication::getLoanTerm,
                        LoanApplication::getLoanApplicationId,
                        LoanApplication::getLoanStatus,
                        LoanApplication::getCreateAt
                )
                .containsExactly(
                        DEFAULT_DOCUMENT_ID,
                        DEFAULT_AMOUNT,
                        DEFAULT_LOAN_TYPE,
                        DEFAULT_LOAN_TERM,
                        null,
                        null,
                        null
                );
    }

    @ParameterizedTest(name = "[{index}] documentId=''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "123-456-789", "123 456 789", "1234567890"})
    @DisplayName("Debe mapear el ID de documento correctamente con valores nulos, vacíos y especiales")
    void shouldMapDocumentIdCorrectlyWithNullEmptyAndSpecialValues(String documentId) {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .documentId(documentId)
                .amount(DEFAULT_AMOUNT)
                .loanType(DEFAULT_LOAN_TYPE)
                .loanTerm(DEFAULT_LOAN_TERM)
                .build();

        LoanApplication result = LoanMapper.toDomain(request);

        assertThat(result)
                .isNotNull()
                .extracting(
                        LoanApplication::getDocumentId,
                        LoanApplication::getAmount,
                        LoanApplication::getLoanType,
                        LoanApplication::getLoanTerm
                )
                .containsExactly(
                        documentId,
                        DEFAULT_AMOUNT,
                        DEFAULT_LOAN_TYPE,
                        DEFAULT_LOAN_TERM
                );
    }

    @Test
    @DisplayName("Debe mapear todos los campos a null")
    void shouldMapAllFieldsToNull() {
        LoanApplicationRequest request = new LoanApplicationRequest();

        LoanApplication result = LoanMapper.toDomain(request);

        assertThat(result)
                .isNotNull();
        assertNull(result.getDocumentId());
        assertNull(result.getAmount());
        assertNull(result.getLoanType());
        assertNull(result.getLoanTerm());
        assertNull(result.getLoanApplicationId());
        assertNull(result.getLoanStatus());
        assertNull(result.getCreateAt());
    }

    @ParameterizedTest(name = "Tipo de préstamo: {0}, Monto: {1}, Plazo: {2}")
    @CsvSource({
            "HIPOTECARIO, 500000000, 300",
            "PERSONAL, 5000000, 36",
            "VEHICULO, 30000000, 60",
            "ESTUDIANTIL, 15000000, 120",
            "COMERCIAL, 100000000, 240",
            "MICROCREDITO, 500000, 12"
    })
    @DisplayName("Debe mapear correctamente diferentes tipos de préstamo con sus valores típicos")
    void shouldMapDifferentLoanTypesWithTypicalValues(String loanType, BigDecimal amount, int loanTerm) {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .documentId(DEFAULT_DOCUMENT_ID)
                .amount(amount)
                .loanType(loanType)
                .loanTerm(loanTerm)
                .build();

        LoanApplication result = LoanMapper.toDomain(request);

        assertThat(result)
                .isNotNull()
                .extracting(
                        LoanApplication::getDocumentId,
                        LoanApplication::getAmount,
                        LoanApplication::getLoanType,
                        LoanApplication::getLoanTerm
                )
                .containsExactly(
                        DEFAULT_DOCUMENT_ID,
                        amount,
                        loanType,
                        loanTerm
                );
    }

    @ParameterizedTest(name = "Monto: {0}")
    @MethodSource("amountValuesProvider")
    @DisplayName("Debe mapear montos de préstamo correctamente, incluyendo valores de borde y decimales")
    void shouldMapLoanAmountsCorrectly(BigDecimal amount) {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .documentId(DEFAULT_DOCUMENT_ID)
                .amount(amount)
                .loanType(DEFAULT_LOAN_TYPE)
                .loanTerm(DEFAULT_LOAN_TERM)
                .build();

        LoanApplication result = LoanMapper.toDomain(request);

        assertThat(result)
                .isNotNull()
                .extracting(
                        LoanApplication::getAmount,
                        LoanApplication::getDocumentId,
                        LoanApplication::getLoanType,
                        LoanApplication::getLoanTerm
                )
                .containsExactly(
                        amount,
                        DEFAULT_DOCUMENT_ID,
                        DEFAULT_LOAN_TYPE,
                        DEFAULT_LOAN_TERM
                );
    }

    private static Stream<Arguments> amountValuesProvider() {
        return Stream.of(
                Arguments.of(BigDecimal.ZERO),
                Arguments.of(new BigDecimal("1")),
                Arguments.of(new BigDecimal("-1")),
                Arguments.of(new BigDecimal("100000")),
                Arguments.of(new BigDecimal("100000.5")),
                Arguments.of(new BigDecimal("100000.9999999999"))
        );
    }

    @ParameterizedTest(name = "Plazo: {0}")
    @ValueSource(ints = {
            Integer.MIN_VALUE, -1, 0, 1, 6, 120, 240, Integer.MAX_VALUE
    })
    @DisplayName("Debe mapear plazos de préstamo correctamente, incluyendo valores de borde y negativos")
    void shouldMapLoanTermsCorrectly(int loanTerm) {

        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .documentId(DEFAULT_DOCUMENT_ID)
                .amount(DEFAULT_AMOUNT)
                .loanType(DEFAULT_LOAN_TYPE)
                .loanTerm(loanTerm)
                .build();

        LoanApplication result = LoanMapper.toDomain(request);

        assertThat(result)
                .isNotNull()
                .extracting(
                        LoanApplication::getLoanTerm,
                        LoanApplication::getDocumentId,
                        LoanApplication::getAmount,
                        LoanApplication::getLoanType
                )
                .containsExactly(
                        loanTerm,
                        DEFAULT_DOCUMENT_ID,
                        DEFAULT_AMOUNT,
                        DEFAULT_LOAN_TYPE
                );
    }
}