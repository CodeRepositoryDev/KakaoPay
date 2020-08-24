package com.kakao.pay.sprinkle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
}
