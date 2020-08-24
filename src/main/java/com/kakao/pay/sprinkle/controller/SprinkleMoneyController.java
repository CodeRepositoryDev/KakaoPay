package com.kakao.pay.sprinkle.controller;

import com.kakao.pay.sprinkle.exception.DeniedUserException;
import com.kakao.pay.sprinkle.exception.EmptyMoneyException;
import com.kakao.pay.sprinkle.exception.TimeOverException;
import com.kakao.pay.sprinkle.model.ApiResponse;
import com.kakao.pay.sprinkle.model.ReceiveMoneyRequest;
import com.kakao.pay.sprinkle.model.SearchSprinkleRequest;
import com.kakao.pay.sprinkle.model.SprinkleMoneyRequest;
import com.kakao.pay.sprinkle.service.SprinkleMoneyService;
import com.kakao.pay.sprinkle.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class SprinkleMoneyController {
    private SprinkleMoneyService sprinkleMoneyService;

    @Autowired
    public SprinkleMoneyController(SprinkleMoneyService sprinkleMoneyService) {
        this.sprinkleMoneyService = sprinkleMoneyService;
    }

    @PostMapping("/sprinkle/money")
    public ApiResponse sprinkleMoney(@RequestHeader("X-ROOM-ID") String roomId, @RequestHeader("X-USER-ID") Integer userId, @RequestBody SprinkleMoneyRequest request) {
        return ApiResponse.builder()
                .success(true)
                .data(sprinkleMoneyService.sprinkleMoney(roomId, userId, request))
                .build();
    }

    @PutMapping("/receive/money")
    public ApiResponse receiveMoney(@RequestHeader("X-ROOM-ID") String roomId, @RequestHeader("X-USER-ID") Integer userId, @RequestBody ReceiveMoneyRequest request) {
        return ApiResponse.builder()
                .success(true)
                .data(sprinkleMoneyService.receiveMoney(roomId, userId, request.getToken()))
                .build();
    }

    @GetMapping("/search/money")
    public ApiResponse searchMySprinkleMoney(@RequestHeader("X-USER-ID") Integer userId, SearchSprinkleRequest request) {
        return ApiResponse.builder()
                .success(true)
                .data(sprinkleMoneyService.searchSprinkleResponse(userId, request.getToken()))
                .build();
    }

    @ExceptionHandler(TimeOverException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse timeOverException(TimeOverException exception) {
        log.error("[API-FAIL]", exception);
        return ApiResponse.builder()
                .success(false)
                .errorCode(ErrorCode.TIME_OVER.getErrorCode())
                .errorMessage(ErrorCode.TIME_OVER.getErrorMessage())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse illegalArgumentException(IllegalArgumentException exception) {
        log.error("[API-FAIL]", exception);
        return ApiResponse.builder()
                .success(false)
                .errorCode(ErrorCode.DIFFERENT_ROOM_ID.getErrorCode())
                .errorMessage(ErrorCode.DIFFERENT_ROOM_ID.getErrorMessage())
                .build();
    }

    @ExceptionHandler(DeniedUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse deniedReceiverException(DeniedUserException exception) {
        log.error("[API-FAIL]", exception);
        return ApiResponse.builder()
                .success(false)
                .errorCode(ErrorCode.DENIED_RECEIVER.getErrorCode())
                .errorMessage(ErrorCode.DENIED_RECEIVER.getErrorMessage())
                .build();
    }

    @ExceptionHandler(EmptyMoneyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse emptyMoneyException(EmptyMoneyException exception) {
        log.error("[API-FAIL]", exception);
        return ApiResponse.builder()
                .success(false)
                .errorCode(ErrorCode.EMPTY_MONEY.getErrorCode())
                .errorMessage(ErrorCode.EMPTY_MONEY.getErrorMessage())
                .build();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse illegalStateException(IllegalStateException exception) {
        log.error("[API-FAIL]", exception);
        return ApiResponse.builder()
                .success(false)
                .errorCode(ErrorCode.INTERNAL_ERROR.getErrorCode())
                .errorMessage(ErrorCode.INTERNAL_ERROR.getErrorMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse internalError(Exception exception) {
        log.error("[API-FAIL]", exception);
        return ApiResponse.builder()
                .success(false)
                .errorCode(ErrorCode.INTERNAL_ERROR.getErrorCode())
                .errorMessage(ErrorCode.INTERNAL_ERROR.getErrorMessage())
                .build();
    }
}
