package com.rawr.springsecutiryapp.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
//@PreAuthorize(("denyAll()")) - > denegar todos por defecto
public class TestAuthController {


    @GetMapping("/hello")
//    @PreAuthorize("permitAll()")
    public String hello(){
        return "Hello";
    }

    @GetMapping("/hello-secured")
    //    @PreAuthorize("hasAuthority('READ')")
    public String helloSecured(){
        return "Hello Secured";
    }

    @GetMapping("/hello-secured2")
    public String helloSecured2(){
        return "Hello Secured";
    }


}
