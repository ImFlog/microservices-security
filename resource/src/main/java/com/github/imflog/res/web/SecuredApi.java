package com.github.imflog.res.web;

import java.util.Collections;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
  public ResponseEntity<String> withARole() {
    var token = ((SimpleKeycloakAccount) SecurityContextHolder.getContext().getAuthentication()
        .getDetails()).getKeycloakSecurityContext().getTokenString();

    var restTemplate = new RestTemplate();
    var headers = new HttpHeaders();
    headers.setBearerAuth(token);

    var entity = new HttpEntity(headers);

    return restTemplate.exchange("http://localhost:5000/secured/role", HttpMethod.GET, entity, String.class);
  }
}
