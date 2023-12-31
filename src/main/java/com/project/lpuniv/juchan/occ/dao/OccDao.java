package com.project.lpuniv.juchan.occ.dao;

import com.project.lpuniv.juchan.occ.dto.OccDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OccDao {

    void occInsert(OccDto occDto);
    Integer occAllCount();

    List<OccDto> occAllSelectDesc();
    List<OccDto> occAllSelectDescPage(@Param("startRow") int startRow, @Param("size") int size);

    OccDto occOneSelect(@Param("occ_NO")int occ_NO);

    void occModify(OccDto occDto);
}
