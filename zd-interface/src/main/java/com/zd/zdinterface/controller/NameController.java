package com.zd.zdinterface.controller;

import com.zd.zdapiclientsdk.model.User;
import com.zd.zdapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * 查询名称
 * @author zd
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/")
    public  String getNameByGet(String name) {
        return "GET 你的名字是：" + name;
    }
    @PostMapping("/")
    public  String getNameByPost(@RequestParam String name) {
        return "Post 你的名字是：" + name;
    }
    @PostMapping("/user")
    public  String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
//        String accessKey = request.getHeader("accessKey");
//        String nonce = request.getHeader("nonce");
//        String timestamp = request.getHeader("timestamp");
//        String sign = request.getHeader("sign");
//        String body = new String(request.getHeader("body").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
//        // todo 数据库中查是否已分配给用户
//        if (!accessKey.equals("12345")) {
//            throw new RuntimeException("无权限");
//        }
//        if (Long.parseLong(nonce) > 10000) {
//            throw new RuntimeException("无权限");
//        }
////         todo 时间和当前时间不能超过 5 分钟
//        if (timestamp.isEmpty()) {
//
//        }
////         todo 实际情况中是从数据库中查出 secretKey
//        String serverSign = SignUtils.genSign(body, "abcdef");
//        if (!sign.equals(serverSign)) {
//            throw new RuntimeException("无权限");
//        }
//         todo 调用次数 + 1 invokeCount

//        if(!accessKey.equals("12345")||!secretKey.equals("abcdef")){
//            throw new RuntimeException("无权限");
//        }
        return user.getUsername();
    }
}
