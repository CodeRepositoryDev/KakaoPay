package com.kakao.pay.sprinkle.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class SearchSprinkleResponse {
    private LocalDateTime sprinkleDateTime;
    private int sprinkleMoney;
    private int receivedMoney;
    private List<ReceivedMoney> receiveMoneyList;
}
