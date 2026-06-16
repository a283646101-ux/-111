package com.zzyl.mapper;

import com.zzyl.entity.Bed;
import com.zzyl.vo.BedVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BedMapper {

    List<BedVo> getBedsByRoomId(Long roomId);

    /**
     * 增加床位
     *
     * @param bed 床位对象
     */
    void addBed(Bed bed);

    /**
     * 根据id删除床位
     *
     * @param id 床位id
     */
    void deleteBedById(Long id);

    /**
     * 更新床位信息
     *
     * @param bed 床位对象
     */
    void updateBed(Bed bed);

    /**
     * 根据id获取床位信息
     *
     * @param id 床位id
     * @return 床位对象
     */
    Bed getBedById(Long id);
}
