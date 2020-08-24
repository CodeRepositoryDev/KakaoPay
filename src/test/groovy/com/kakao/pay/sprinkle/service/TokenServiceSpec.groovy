package com.kakao.pay.sprinkle.service

import spock.lang.Specification

class TokenServiceSpec extends Specification{
    def sut = new TokenService()

    def "getTokenSpec"() {
        when:
        def token = sut.getToken()

        then:
        token != null
        token.length() == sut.TOKEN_SIZE
    }
}
