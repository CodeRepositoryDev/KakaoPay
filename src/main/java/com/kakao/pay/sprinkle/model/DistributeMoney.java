package com.kakao.pay.sprinkle.model;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DistributeMoney {
    private String token;
    private int seq;
    private int distributeMoney;
}
