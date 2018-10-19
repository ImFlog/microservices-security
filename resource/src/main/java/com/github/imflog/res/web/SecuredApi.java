package com.github.imflog.res.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secured")
public class SecuredApi {

    @GetMapping
    public String secured() {
        return "You are authenticated as : " + SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PreAuthorize("hasRole('DUMB_ROLE')")
    @GetMapping("/dumb")
    public String dumbTest() {
        return "You should not be able to access this ...";
    }

    @PreAuthorize("hasRole('ROLE_yes_we_can')")
    @GetMapping("/role")
    public String withARole() {
        return "That's right, you can";
    }
}
