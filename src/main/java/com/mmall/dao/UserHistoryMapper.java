package com.mmall.dao;

import com.mmall.pojo.UserHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserHistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserHistory record);

    int insertSelective(UserHistory record);

    UserHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserHistory record);

    int updateByPrimaryKey(UserHistory record);

    UserHistory selectUserViewHistoryByUserIdProductId(@Param(value = "userId") Integer userId, @Param(value = "productId") Integer productId);

    List<UserHistory> selectUserViewHistoryByUserIdCategoryId(@Param(value = "userId") Integer userId, @Param(value = "num") Integer num, @Param(value = "categoryId")Integer categoryId);
}