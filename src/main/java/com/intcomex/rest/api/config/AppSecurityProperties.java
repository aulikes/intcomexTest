package com.intcomex.rest.api.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "security")
public class AppSecurityProperties {
    private List<User> users;

    @Setter
    @Getter
    public static class User {
        private String username;
        private String password;
        private List<String> roles;
    }
}

