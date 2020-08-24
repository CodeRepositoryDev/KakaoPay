package com.kakao.pay.sprinkle.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SprinkleMoney {
    private String token;
    private String roomId;
    private Integer sprinkleMoney;
    private Integer receiverCount;
    private Integer sprinkleUserId;
    private LocalDateTime sprinkleDateTime;

}
