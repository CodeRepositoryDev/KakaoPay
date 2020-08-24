package com.kakao.pay.sprinkle.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMoney {
    private Integer receiverId;
    private Integer receiveMoney;
}
