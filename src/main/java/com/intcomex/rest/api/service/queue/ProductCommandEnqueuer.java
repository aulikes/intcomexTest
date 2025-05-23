package com.intcomex.rest.api.service.queue;

import com.intcomex.rest.api.dto.ProductCreateRequest;

public interface ProductCommandEnqueuer {
    void enqueue(ProductCreateRequest request);
}
