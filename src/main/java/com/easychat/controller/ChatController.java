package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.vo.ResponseVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/chat")
public class ChatController extends ABaseController {


    @RequestMapping("/sendMessage")
    @GlobalInterceptor
    public ResponseVo sendMessage(){

        return getSuccessResponseVo(null);
    }
}
