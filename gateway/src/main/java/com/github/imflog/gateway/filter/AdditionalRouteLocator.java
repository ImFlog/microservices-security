package com.github.imflog.gateway.filter;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.ServiceRouteMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Conditional class which will work in case of <code>channelManagementEnabled</code> property is {@literal false} in
 * configuration. This class just add more user defined routes.
 */
@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "routing")
@ConditionalOnProperty(name = "channelManagementEnabled", havingValue = "false")
public class AdditionalRouteLocator extends DiscoveryClientRouteLocator {

    /* Additional routes, placed in property file */
    private Map<String, ZuulRoute> additionalRoutes = new LinkedHashMap<>();

    @Autowired
    public AdditionalRouteLocator(ServerProperties server, DiscoveryClient discovery,
                                  ZuulProperties properties, ServiceRouteMapper serviceRouteMapper) {
        super(server.getServlet().getServletPrefix(), discovery, properties, serviceRouteMapper, null);
    }

    /**
     * We need to follow conventions defined in {@link ZuulProperties} class
     */
    @PostConstruct
    public void init() {
        log.debug("User define some additional routes:\n {}", additionalRoutes);
        for (Map.Entry<String, ZuulRoute> entry : this.additionalRoutes.entrySet()) {
            ZuulRoute value = entry.getValue();
            if (!StringUtils.hasText(value.getLocation())) {
                value.setServiceId(entry.getKey());
            }
            if (!StringUtils.hasText(value.getId())) {
                value.setId(entry.getKey());
            }
            if (!StringUtils.hasText(value.getPath())) {
                value.setPath("/" + entry.getKey() + "/**");
            }
        }
    }

    /**
     * Collects all default {@link ZuulRoute} and adds some additional
     *
     * @return {@link LinkedHashMap} with key as route url, and value as zuul route
     */
    @Override
    protected LinkedHashMap<String, ZuulRoute> locateRoutes() {
        log.debug("Additional Zuul routes overrides default routes");
        LinkedHashMap<String, ZuulRoute> routesMap = super.locateRoutes();
        routesMap.putAll(this.additionalRoutes.values().stream()
                .collect(Collectors.toMap(ZuulRoute::getPath, Function.identity(),
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Route path '%s' shouldn't duplicated", u));
                        },
                        LinkedHashMap::new)));
        return routesMap;
    }
}