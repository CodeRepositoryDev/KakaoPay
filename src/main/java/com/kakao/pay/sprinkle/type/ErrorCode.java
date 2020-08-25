package com.kakao.pay.sprinkle.type;

public enum ErrorCode {
    INTERNAL_ERROR("INTERNAL_ERROR", "SERVER ERROR"),
    INVALID_PARAMETER("INVALID_PARAMETER","INVALID PARAMETER"),
    TIME_OVER("TIME_OVER","TIME OVER"),
    EMPTY_MONEY("EMPTY_MONEY","EMPTY MONEY"),
    DENIED_RECEIVER("DENIED_RECEIVER", "DENIED RECEIVER"),
    DIFFERENT_ROOM_ID("DIFFERENT_ROOM_ID", "DIFFERENT ROOM ID");

    private String errorCode;
    private String errorMessage;

    ErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
