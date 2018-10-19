package com.github.imflog.res.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
class TestController {

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/auth")
    public String test(HttpServletRequest request) {
        return "You are correctly authenticated";
    }

    @PreAuthorize("hasRole('DUMB_ROLE')")
    @RequestMapping(path = "/dumb")
    public String dumbTest() {
        return "You should not be able to access this ...";
    }

    @PreAuthorize("hasRole('ROLE_YOU_CAN')")
    @RequestMapping(path = "/role")
    public String withARole() {
        return "That's right, you can";
    }
}
