package com.intcomex.rest.api.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

public class UrlBuilderUtil {

    /**
     * Construye la URL absoluta para un recurso, dado un request y una ruta relativa.
     */
    public static String buildAbsoluteUrl(@NotNull HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        return scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);
    }
}
