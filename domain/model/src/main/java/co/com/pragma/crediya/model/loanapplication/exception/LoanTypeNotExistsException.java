package co.com.pragma.crediya.model.loanapplication.exception;

public class LoanTypeNotExistsException extends RuntimeException{

    public LoanTypeNotExistsException(String loadType){

        super("No existe el tipo de prestamo: ".concat(loadType));
    }
}
