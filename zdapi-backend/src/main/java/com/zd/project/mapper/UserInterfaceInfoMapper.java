package com.zd.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zd.zdcommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author zdsss
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2023-03-22 18:22:00
* @Entity generator.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




