package com.github.imflog.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
class KeycloakFilterRoute extends ZuulFilter {

    private static final String FILTER_TYPE = "route";
    private static final int FILTER_ORDER = 0;

    private static final String AUTHORIZATION_HEADER = "authorization";


    @Override
    public String filterType() {
        return FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRequest().getHeader(AUTHORIZATION_HEADER) != null) {
            addKeycloakTokenToHeader(ctx);
        }
        return null;
    }

    private void addKeycloakTokenToHeader(RequestContext ctx) {
        RefreshableKeycloakSecurityContext securityContext = getRefreshableKeycloakSecurityContext(ctx);
        if (securityContext != null) {
            ctx.addZuulRequestHeader(AUTHORIZATION_HEADER, buildBearerToken(securityContext));
        }
    }

    private RefreshableKeycloakSecurityContext getRefreshableKeycloakSecurityContext(RequestContext ctx) {
        if (ctx.getRequest().getUserPrincipal() instanceof KeycloakAuthenticationToken) {
            KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) ctx.getRequest().getUserPrincipal();
            return (RefreshableKeycloakSecurityContext) token.getCredentials();
        }
        return null;
    }

    private String buildBearerToken(RefreshableKeycloakSecurityContext securityContext) {
        return "Bearer " + securityContext.getTokenString();
    }
}