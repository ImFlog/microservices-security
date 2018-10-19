package com.github.imflog.res.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OpenApi {

    @GetMapping("/")
    public String test(HttpServletRequest request) {
        return "You are on the open API";
    }
}
