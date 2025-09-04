package co.com.pragma.crediya.model.loanapplication.helper;

import co.com.pragma.crediya.model.loanapplication.exception.InvalidAmountRangeException;

import java.math.BigDecimal;

public class AmountValidator {

    private AmountValidator() {}

    public static void validate(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountRangeException("El Monto no puede ser nulo");
        }

        if ( amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountRangeException(
                    amount.toPlainString()
            );
        }
    }
}
