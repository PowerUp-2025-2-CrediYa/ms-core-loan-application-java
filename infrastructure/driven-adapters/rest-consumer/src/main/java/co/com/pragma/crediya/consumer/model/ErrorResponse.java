package co.com.pragma.crediya.consumer.model;

public record ErrorResponse(
        String error,
        String message,
        String path,
        Integer status,
        String timestamp
){}


