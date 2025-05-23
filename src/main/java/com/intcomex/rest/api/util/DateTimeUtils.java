package com.intcomex.rest.api.util;

import com.intcomex.rest.api.config.ZoneContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(ZoneContextHolder.getZoneId());
    }

    public static LocalDateTime nowLocal() {
        return LocalDateTime.now(ZoneContextHolder.getZoneId());
    }

    public static ZoneId currentZone() {
        return ZoneContextHolder.getZoneId();
    }
}
