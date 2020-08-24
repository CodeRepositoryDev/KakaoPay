package com.kakao.pay.sprinkle.exception;

public class TimeOverException extends RuntimeException {
    public TimeOverException(){
        super();
    }

    public TimeOverException(String message) {
        super(message);
    }


    public TimeOverException(String message, Throwable cause) {
        super(message, cause);
    }
}
