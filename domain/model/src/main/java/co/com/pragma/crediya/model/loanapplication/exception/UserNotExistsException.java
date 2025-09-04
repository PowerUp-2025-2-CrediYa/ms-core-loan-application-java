package co.com.pragma.crediya.model.loanapplication.exception;

public class UserNotExistsException extends RuntimeException{

    public UserNotExistsException(String msg){
        super(msg);
    }
}
