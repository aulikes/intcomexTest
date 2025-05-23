package com.intcomex.rest.api.service.contract;

import com.intcomex.rest.api.dto.AuthRequest;
import com.intcomex.rest.api.dto.AuthResponse;

public interface AuthService {

    public AuthResponse authenticate(AuthRequest request);
}
