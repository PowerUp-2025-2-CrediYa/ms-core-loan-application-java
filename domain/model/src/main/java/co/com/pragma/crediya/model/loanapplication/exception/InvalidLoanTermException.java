package co.com.pragma.crediya.model.loanapplication.exception;

public class InvalidLoanTermException extends RuntimeException{

    public InvalidLoanTermException(String loanTerm){
        super("Plazo Invalido: ".concat(loanTerm));
    }
}