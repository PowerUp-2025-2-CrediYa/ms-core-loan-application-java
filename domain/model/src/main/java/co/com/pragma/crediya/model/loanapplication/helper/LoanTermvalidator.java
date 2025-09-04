package co.com.pragma.crediya.model.loanapplication.helper;

import co.com.pragma.crediya.model.loanapplication.exception.InvalidAmountRangeException;
import co.com.pragma.crediya.model.loanapplication.exception.InvalidLoanTermException;

public class LoanTermvalidator {

    private LoanTermvalidator(){}

    public static void validate(Integer loanTerm) {

        if (loanTerm == null) {
            throw new InvalidAmountRangeException("El Monto no puede ser nulo");
        }

        if ( loanTerm <= 0 ) {
            throw new InvalidLoanTermException(
                    String.valueOf(loanTerm)
            );
        }
    }

}
