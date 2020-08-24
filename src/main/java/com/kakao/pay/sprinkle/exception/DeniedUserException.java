package com.kakao.pay.sprinkle.exception;

public class DeniedUserException extends RuntimeException {
    public DeniedUserException(){
        super();
    }

    public DeniedUserException(String message) {
        super(message);
    }


    public DeniedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
