package co.com.pragma.crediya.model.loanapplication.exception;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String msg){
        super(msg);
    }
}
