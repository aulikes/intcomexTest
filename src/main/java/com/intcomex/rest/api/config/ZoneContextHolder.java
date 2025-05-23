package com.intcomex.rest.api.config;

import java.time.ZoneId;

public class ZoneContextHolder {

    private static final ThreadLocal<ZoneId> CONTEXT = new ThreadLocal<>();

    private ZoneContextHolder(){}

    public static void setZoneId(ZoneId zoneId) {
        CONTEXT.set(zoneId);
    }

    public static ZoneId getZoneId() {
        ZoneId zoneId = CONTEXT.get();
        return zoneId != null ? zoneId : ZoneId.of("UTC"); // Fallback seguro
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
