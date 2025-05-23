package com.intcomex.rest.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZoneId;

@Component
@Slf4j
public class TimeZoneFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;

    public TimeZoneFilter(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String headerName = appProperties.getCustomerZone().getHeaderComponent();
        String headerZone = request.getHeader(headerName);
        ZoneId zone = ZoneId.of(appProperties.getCustomerZone().getTimezone()); // fallback

        if (headerZone != null && !headerZone.isBlank()) {
            try {
                zone = ZoneId.of(headerZone);
            } catch (Exception ex) {
                log.debug("NOT Zone", ex);
            }
        }
        ZoneContextHolder.setZoneId(zone);

        try {
            filterChain.doFilter(request, response);
        } finally {
            ZoneContextHolder.clear();
        }
    }
}
