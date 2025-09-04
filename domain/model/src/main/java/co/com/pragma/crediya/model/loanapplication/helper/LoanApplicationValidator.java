package co.com.pragma.crediya.model.loanapplication.helper;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.exception.InvalidLoanException;

public class LoanApplicationValidator {

    private LoanApplicationValidator(){}

    public static void validate(LoanApplication loanApplication){

        if (isNullOrEmpty(loanApplication.getDocumentId())) {
            throw new InvalidLoanException("El numero de identificación no puede estar vacío");
        }

        if (isNullOrEmpty(loanApplication.getLoanType())) {
            throw new InvalidLoanException("El tipo de prestamo no puede estar vacío");
        }

        if (isNullOrEmpty(String.valueOf(loanApplication.getLoanTerm()))) {
            throw new InvalidLoanException("El plazo no puede estar vacío");
        }

        if (isNullOrEmpty(String.valueOf(loanApplication.getAmount()))) {
            throw new InvalidLoanException("El monto no puede estar vacío");
        }

        /*EmailValidator.validate(user.getEmail());
        SalaryValidator.validate(user.getBaseSalary());
        AddressValidator.validate(user.getAddress());
        BirthDateValidator.validate(String.valueOf(user.getBirthDate()));*/

    }

    private static boolean isNullOrEmpty(String value) {

        return value == null || value.trim().isEmpty();
    }
}
