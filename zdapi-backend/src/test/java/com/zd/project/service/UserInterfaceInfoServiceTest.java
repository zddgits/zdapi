package com.zd.project.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
 *
 * @author zd
 */
@SpringBootTest
class UserInterfaceInfoServiceTest {
    @Resource
    UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public  void testInvokeCount() {
        boolean b = userInterfaceInfoService.invokeCount(1L,1L);
        Assertions.assertTrue(b);
    }

}