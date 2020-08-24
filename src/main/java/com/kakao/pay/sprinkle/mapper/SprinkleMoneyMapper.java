package com.kakao.pay.sprinkle.mapper;

import com.kakao.pay.sprinkle.model.DistributeMoney;
import com.kakao.pay.sprinkle.model.ReceivedMoney;
import com.kakao.pay.sprinkle.model.SprinkleMoney;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SprinkleMoneyMapper {
    void insertSprinkleMoney(SprinkleMoney sprinkleMoney);

    void insertDistributeMoney(DistributeMoney distributeMoney);

    SprinkleMoney selectSprinkleMoney(String token);

    List<ReceivedMoney> selectReceivedMoneyList(String token);

    DistributeMoney selectDistributeMoney(String token);

    boolean selectAlreadyReceiverId(@Param("token") String token, @Param("receiverId") Integer receiverId);

    void updateReceiveDistributeMoney(@Param("distributeMoney") DistributeMoney distributeMoney, @Param("receiverId") Integer receiverId);
}
