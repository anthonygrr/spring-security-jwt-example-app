package com.rawr.springsecutiryapp.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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


//    Nuevos endpoints
    @GetMapping("/get")
    public String helloGet(){
        return "Hello World - GET";
    }

    @PostMapping("/post")
    public String helloPost(){
        return "Hello World - POST";
    }

    @PutMapping("/put")
    public String helloPut(){
        return "Hello World - PUT";
    }

    @DeleteMapping("/delete")
    public String helloDelete(){
        return "Hello World - DELETE";
    }

    @PatchMapping("/patch")
    public String helloPatch(){
        return "Hello World - PATCH";
    }

}
