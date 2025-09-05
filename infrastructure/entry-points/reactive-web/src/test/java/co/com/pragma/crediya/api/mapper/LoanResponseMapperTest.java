package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.model.response.LoanApplicationResponse;
import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LoanResponseMapper Test")
class LoanResponseMapperTest{

    @Test
    @DisplayName("should map LoanApplication to LoanApplicationResponse correctly")
    void shouldMapLoanApplicationToResponse() {
        // Arrange
        LoanApplication loanApplication = LoanApplication.builder()
                .loanApplicationId(UUID.fromString("9c6f12cf-4c2e-45be-b8de-6cf4721b56cc"))
                .documentId("DOC-456")
                .amount(BigDecimal.valueOf(10000))
                .loanType("PERSONAL")
                .loanTerm(24)
                .loanStatus("APPROVED")
                .build();

        // Act
        LoanApplicationResponse response = LoanResponseMapper.fromDomain(loanApplication);

        // Assert
        assertAll("Mapped fields",
                () -> assertEquals(UUID.fromString("9c6f12cf-4c2e-45be-b8de-6cf4721b56cc"), response.getLoanApplicationId()),
                () -> assertEquals("DOC-456", response.getDocumentId()),
                () -> assertEquals(BigDecimal.valueOf(10000), response.getAmount()),
                () -> assertEquals("PERSONAL", response.getLoanType()),
                () -> assertEquals(24, response.getLoanTerm()),
                () -> assertEquals("APPROVED", response.getLoanStatus())
        );
    }

}
