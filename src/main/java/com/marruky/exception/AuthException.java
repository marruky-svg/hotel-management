package com.marruky.exception;

public class AuthException extends HotelException{

    public AuthException(String message){
        super(message);
    }

    public AuthException(String message, Throwable cause){
        super(message, cause);
    }
}
