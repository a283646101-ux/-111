package com.zzyl.mapper;

import com.zzyl.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客户Mapper接口
 */
@Mapper
public interface MemberMapper {

    /**
     * 根据openId查询客户
     * @param openId 微信openId
     * @return 客户信息
     */
    Member getByOpenId(@Param("openId") String openId);

    /**
     * 新增客户
     * @param member 客户信息
     */
    void add(Member member);

    /**
     * 更新客户信息
     * @param member 客户信息
     */
    void update(Member member);

    /**
     * 根据id查询客户
     * @param id 客户id
     * @return 客户信息
     */
    Member getById(@Param("id") Long id);
}
