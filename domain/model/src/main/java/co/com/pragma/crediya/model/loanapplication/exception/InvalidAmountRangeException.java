package co.com.pragma.crediya.model.loanapplication.exception;

public class InvalidAmountRangeException extends RuntimeException{

    public InvalidAmountRangeException(String amount){

        super("Monto fuera del rango: ".concat(amount));
    }
}
