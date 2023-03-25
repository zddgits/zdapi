package com.zd.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zd.zdcommon.model.entity.UserInterfaceInfo;

/**
* @author zdsss
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-03-22 18:22:00
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);
    boolean invokeCount(long interfaceInfoId, long userId);
}
