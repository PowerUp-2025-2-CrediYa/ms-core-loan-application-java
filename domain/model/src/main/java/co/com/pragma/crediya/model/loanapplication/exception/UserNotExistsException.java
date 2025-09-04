package co.com.pragma.crediya.model.loanapplication.exception;

public class UserNotExistsException extends RuntimeException{

    public UserNotExistsException(String documentId){
        super("El usuario con identificación: ".concat(documentId).concat(" no existe"));
    }
}
