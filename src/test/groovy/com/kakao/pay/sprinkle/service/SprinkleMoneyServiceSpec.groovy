package com.kakao.pay.sprinkle.service

import com.kakao.pay.sprinkle.exception.DeniedUserException
import com.kakao.pay.sprinkle.exception.EmptyMoneyException
import com.kakao.pay.sprinkle.exception.TimeOverException
import com.kakao.pay.sprinkle.mapper.SprinkleMoneyMapper
import com.kakao.pay.sprinkle.model.DistributeMoney
import com.kakao.pay.sprinkle.model.ReceivedMoney
import com.kakao.pay.sprinkle.model.SprinkleMoney
import com.kakao.pay.sprinkle.model.SprinkleMoneyRequest
import spock.lang.Specification

import java.time.LocalDateTime

class SprinkleMoneyServiceSpec extends Specification {
    def mapper = Mock(SprinkleMoneyMapper.class)
    def tokenService = Mock(TokenService.class)

    def sut = new SprinkleMoneyService(tokenService, mapper)

    def "카카오페이 뿌리기"() {
        given:
        def token = "Lqt"
        def roomId = "test1234"
        def userId = 31337
        def sprinkleMoneyRequest = new SprinkleMoneyRequest(sprinkleMoney: 20000, receiverCount: 4)

        when: "정상적으로 뿌리기 성공"
        sut.sprinkleMoney(roomId, userId, sprinkleMoneyRequest)

        then:
        1 * tokenService.getToken() >> token
        1 * mapper.insertSprinkleMoney(_)
        sprinkleMoneyRequest.receiverCount * mapper.insertDistributeMoney(_)
    }

    def "카카오페이 뿌리기 받기"() {
        given:
        def token = "Tes"
        def roomId = "room"
        def userId = 31337
        def notSameRoomSprinkleMoney = new SprinkleMoney(roomId: "room2")
        def timeOverSprinkleMoney = new SprinkleMoney(roomId: "room", sprinkleDateTime: LocalDateTime.now().minusMinutes(11))
        def ownerReceiveSprinkleMoney = new SprinkleMoney(roomId: "room", sprinkleDateTime: LocalDateTime.now().minusMinutes(4), sprinkleUserId: userId)
        def alreadyReceiveSprinkleMoney = new SprinkleMoney(roomId: "room", sprinkleDateTime: LocalDateTime.now().minusMinutes(4), sprinkleUserId: 1)
        def noMoreReceiveSprinkleMoney = new SprinkleMoney(roomId: "room", sprinkleDateTime: LocalDateTime.now().minusMinutes(4), sprinkleUserId: 1)
        def distributeMoney = new DistributeMoney(token: token, seq: 0, distributeMoney: 15000)
        def successReceiveSprinkleMoney = new SprinkleMoney(roomId: "room", sprinkleDateTime: LocalDateTime.now().minusMinutes(4), sprinkleUserId: 1)

        when: "같은 방의 뿌리기 정보가 아닐 때"
        sut.receiveMoney(roomId, userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> notSameRoomSprinkleMoney
        thrown(IllegalArgumentException.class)

        when: "뿌리고 10분이 지나 받을 때"
        sut.receiveMoney(roomId, userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> timeOverSprinkleMoney
        thrown(TimeOverException.class)

        when: "뿌린 사람이 받을 때"
        sut.receiveMoney(roomId, userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> ownerReceiveSprinkleMoney
        thrown(DeniedUserException.class)

        when: "이미 받은 사람이 또 받으려고 할 때"
        sut.receiveMoney(roomId, userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> alreadyReceiveSprinkleMoney
        1 * mapper.selectAlreadyReceiverId(token, userId) >> true
        thrown(DeniedUserException.class)

        when: "더 이상 받을 돈이 없을 때"
        sut.receiveMoney(roomId, userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> noMoreReceiveSprinkleMoney
        1 * mapper.selectAlreadyReceiverId(token, userId) >> false
        1 * mapper.selectDistributeMoney(token) >> null
        thrown(EmptyMoneyException.class)

        when: "정상적으로 돈을 받을 "
        def receivedMoney = sut.receiveMoney(roomId, userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> successReceiveSprinkleMoney
        1 * mapper.selectAlreadyReceiverId(token, userId) >> false
        1 * mapper.selectDistributeMoney(token) >> distributeMoney
        1 * mapper.updateReceiveDistributeMoney(distributeMoney, userId)
        receivedMoney == distributeMoney.getDistributeMoney()
    }

    def "카카오 뿌리기 조회"() {
        given:
        def token = "Tes"
        def userId = 31337
        def notExistsSprinkleMoney = null
        def notOwnerSprinkleMoney = new SprinkleMoney(sprinkleUserId: 1)
        def timeOverSprinkleMoney = new SprinkleMoney(sprinkleUserId: userId, sprinkleDateTime: LocalDateTime.now().minusDays(8))
        def successSprinkleMoney1 = new SprinkleMoney(sprinkleUserId: userId, sprinkleDateTime: LocalDateTime.now().minusDays(4), sprinkleMoney: 170000, receiverCount: 2)
        def receivedMoneyList1 = [new ReceivedMoney(receiverId: 2, receiveMoney: 70000), new ReceivedMoney(receiverId: 3, receiveMoney: 100000)]
        def successSprinkleMoney2 = new SprinkleMoney(sprinkleUserId: userId, sprinkleDateTime: LocalDateTime.now().minusDays(4), sprinkleMoney: 170000, receiverCount: 4)
        def receivedMoneyList2 = [new ReceivedMoney(receiverId: 2, receiveMoney: 10000), new ReceivedMoney(receiverId: 3, receiveMoney: 30000)]

        when: "뿌린 정보가 없을 때"
        sut.searchSprinkleResponse(userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> notExistsSprinkleMoney
        thrown(IllegalStateException.class)

        when: "뿌린 정보가 내가 뿌린게 아닐 때"
        sut.searchSprinkleResponse(userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> notOwnerSprinkleMoney
        thrown(DeniedUserException.class)

        when: "뿌린 정보의 유효기간이 지났을 때"
        sut.searchSprinkleResponse(userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> timeOverSprinkleMoney
        thrown(TimeOverException.class)

        when: "정상적으로 조회 (모든 사람이 다 받았을 때)"
        def response = sut.searchSprinkleResponse(userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> successSprinkleMoney1
        1 * mapper.selectReceivedMoneyList(token) >> receivedMoneyList1
        response.sprinkleDateTime == successSprinkleMoney1.sprinkleDateTime
        response.sprinkleMoney == successSprinkleMoney1.sprinkleMoney
        response.receiveMoneyList == receivedMoneyList1
        response.receivedMoney == successSprinkleMoney1.sprinkleMoney

        when: "정상적으로 조회 (모든 사람이 다 못 받았을 때)"
        def response2 = sut.searchSprinkleResponse(userId, token)

        then:
        1 * mapper.selectSprinkleMoney(token) >> successSprinkleMoney2
        1 * mapper.selectReceivedMoneyList(token) >> receivedMoneyList2
        response2.sprinkleDateTime == successSprinkleMoney2.sprinkleDateTime
        response2.sprinkleMoney == successSprinkleMoney2.sprinkleMoney
        response2.receiveMoneyList == receivedMoneyList2
        response2.receivedMoney == receivedMoneyList2.stream().mapToInt{receivedMoney -> receivedMoney.getReceiveMoney()}.sum()
    }
}
