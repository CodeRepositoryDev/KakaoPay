package com.kakao.pay.sprinkle.service;

import com.kakao.pay.sprinkle.exception.DeniedUserException;
import com.kakao.pay.sprinkle.exception.EmptyMoneyException;
import com.kakao.pay.sprinkle.exception.TimeOverException;
import com.kakao.pay.sprinkle.mapper.SprinkleMoneyMapper;
import com.kakao.pay.sprinkle.model.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
public class SprinkleMoneyService {
    private TokenService tokenService;
    private SprinkleMoneyMapper sprinkleMoneyMapper;

    @Autowired
    public SprinkleMoneyService(TokenService tokenService, SprinkleMoneyMapper sprinkleMoneyMapper) {
        this.tokenService = tokenService;
        this.sprinkleMoneyMapper = sprinkleMoneyMapper;
    }

    @Transactional
    public String sprinkleMoney(String roomId, Integer userId, SprinkleMoneyRequest request) {
        String token = tokenService.getToken();
        SprinkleMoney sprinkleMoney = SprinkleMoney.builder()
                .token(token)
                .roomId(roomId)
                .sprinkleUserId(userId)
                .sprinkleMoney(request.getSprinkleMoney())
                .receiverCount(request.getReceiverCount())
                .build();

        sprinkleMoneyMapper.insertSprinkleMoney(sprinkleMoney);
        distributeMoney(sprinkleMoney);

        return token;
    }

    private void distributeMoney(SprinkleMoney sprinkleMoney) {
        Integer receiverCount = sprinkleMoney.getReceiverCount();
        Integer remainMoney = sprinkleMoney.getSprinkleMoney();

        for (int i = 0; i < receiverCount; i++) {
            int money;

            if (i != receiverCount - 1) {
                money = RandomUtils.nextInt(0, remainMoney);
            } else {
                money = remainMoney;
            }

            remainMoney -= money;

            DistributeMoney distributeMoney = DistributeMoney.builder()
                    .token(sprinkleMoney.getToken())
                    .seq(i)
                    .distributeMoney(money)
                    .build();

            sprinkleMoneyMapper.insertDistributeMoney(distributeMoney);
        }

        if (remainMoney != 0) {
            throw new IllegalStateException("[ERROR] distribute money fail.");
        }
    }

    @Transactional
    public int receiveMoney(String roomId, int receiverId, String token) {
        SprinkleMoney sprinkleMoney = sprinkleMoneyMapper.selectSprinkleMoney(token);

        if (!StringUtils.equals(sprinkleMoney.getRoomId(), roomId)) {
            throw new IllegalArgumentException("[INVALID] Room Id different. sprinkleMoney:" + sprinkleMoney + ", requestRoomId: " + roomId);
        }

        if (sprinkleMoney.getSprinkleDateTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new TimeOverException("[INVALID] Time over. sprinkleMoney: " + sprinkleMoney);
        }

        if (sprinkleMoney.getSprinkleUserId() == receiverId) {
            throw new DeniedUserException("[INVALID] SprinkleUser can not receive money. sprinkleMoney: " + sprinkleMoney);
        }

        if (sprinkleMoneyMapper.selectAlreadyReceiverId(token, receiverId)) {
            throw new DeniedUserException("[INVALID] This user already receive money. token: " + token + ", receiverId: " + receiverId);
        }

        DistributeMoney targetReceiveMoney = sprinkleMoneyMapper.selectDistributeMoney(token);

        if (Objects.isNull(targetReceiveMoney)) {
            throw new EmptyMoneyException("[ERROR] Don't receive money. token:" + token);
        }

        sprinkleMoneyMapper.updateReceiveDistributeMoney(targetReceiveMoney, receiverId);

        return targetReceiveMoney.getDistributeMoney();
    }

    public SearchSprinkleResponse searchSprinkleResponse(Integer userId, String token) {
        SprinkleMoney sprinkleMoney = sprinkleMoneyMapper.selectSprinkleMoney(token);

        if (Objects.isNull(sprinkleMoney)) {
            throw new IllegalStateException("[ERROR] Don't receive money. token:" + token);
        }

        if (!sprinkleMoney.getSprinkleUserId().equals(userId)) {
            throw new DeniedUserException("[INVALID] user is not creator. sprinkleMoney: " + sprinkleMoney + ", userId: " + userId);
        }

        if (sprinkleMoney.getSprinkleDateTime().isBefore(LocalDateTime.now().minusDays(7))) {
            throw new TimeOverException("[INVALID] Time over. sprinkleMoney: " + sprinkleMoney);
        }

        List<ReceivedMoney> receivedMoneyList = sprinkleMoneyMapper.selectReceivedMoneyList(token);

        return SearchSprinkleResponse.builder()
                .sprinkleMoney(sprinkleMoney.getSprinkleMoney())
                .sprinkleDateTime(sprinkleMoney.getSprinkleDateTime())
                .receiveMoneyList(receivedMoneyList)
                .receivedMoney(receivedMoneyList.stream().mapToInt(ReceivedMoney::getReceiveMoney).sum())
                .build();
    }
}
