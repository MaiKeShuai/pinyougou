package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/showLoginName")
    public Map showLoginName(){
        Map map = new HashMap();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName",name);
        return map;
    }
}
