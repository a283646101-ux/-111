package com.zzyl.service.impl;

import com.zzyl.entity.Member;
import com.zzyl.mapper.MemberMapper;
import com.zzyl.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 客户服务实现类
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public Member getByOpenId(String openId) {
        return memberMapper.getByOpenId(openId);
    }

    @Override
    public void add(Member member) {
        memberMapper.add(member);
    }

    @Override
    public void update(Member member) {
        memberMapper.update(member);
    }

    @Override
    public Member getById(Long id) {
        return memberMapper.getById(id);
    }
}
