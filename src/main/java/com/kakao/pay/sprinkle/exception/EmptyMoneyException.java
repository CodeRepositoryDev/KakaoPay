package com.kakao.pay.sprinkle.exception;

public class EmptyMoneyException extends RuntimeException {
    public EmptyMoneyException(){
        super();
    }

    public EmptyMoneyException(String message) {
        super(message);
    }
}
