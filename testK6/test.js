//k6 run test.js

import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

const createProductTime = new Trend('create_product_time');

export function setup() {
    // Headers explícitos
    const headers = {
        'Content-Type': 'application/json; charset=UTF-8',
        'Accept': 'application/json'
    };

    const loginRes = http.post('http://localhost:8080/auth/login', JSON.stringify({
        username: 'admin',
        password: 'admin123'
    }), { headers });

    const token = loginRes.json('token');
    return { token };
}

export default function (data) {
    const payload = JSON.stringify({
        productName: 'Servidor Pro X500',
        quantityPerUnit: '1 unidad',
        unitPrice: 14999.99,
        unitsInStock: 20,
        unitsOnOrder: 10,
        reorderLevel: 5,
        discontinued: false,
        categoryID: 1
    });

    // Headers explícitos para el endpoint protegido
    const headers = {
        'Authorization': `Bearer ${data.token}`,
        'Content-Type': 'application/json; charset=UTF-8',
        'Accept': 'application/json'
    };

    const start = Date.now();
    const res = http.post('http://localhost:8080/api/products', payload, { headers });
    const elapsed = Date.now() - start;
    createProductTime.add(elapsed);

    check(res, {
        'status is 202': (r) => r.status === 202
    });
}
