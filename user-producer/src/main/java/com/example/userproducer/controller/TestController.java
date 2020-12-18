package com.example.userproducer.controller;

import com.example.userproducer.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public User initPage(){
        User user = new User();
        user.setUserId("yechanlim");
        user.setName("Yechan");
        user.setAddress("Ansan");
        return user;
    }
}
