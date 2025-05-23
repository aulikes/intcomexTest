import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

const createProductTime = new Trend('create_product_time');

export const options = {
    scenarios: {
        burst_1000_rps: {
            executor: 'constant-arrival-rate',
            rate: 1000,             // 1000 peticiones por segundo
            timeUnit: '1s',
            duration: '1s',
            preAllocatedVUs: 1000,  // VUs preasignados (ajusta si es necesario)
            maxVUs: 2000,
        }
    },
    thresholds: {
        'checks{status is 202}': ['rate>0.99'],
        'create_product_time': ['avg<500'],
    }
};

export function setup() {
    const loginRes = http.post('http://localhost:8080/auth/login', JSON.stringify({
        username: 'admin',
        password: 'admin123'
    }), {
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const token = loginRes.json('token');
    return { token };
}

export default function (data) {
    // Payload para la creación de producto (puedes parametrizarlo)
    const payload = JSON.stringify({
        productName: 'Servidor ' + Math.random().toString(36).substring(2, 10),
        quantityPerUnit: '1 unidad',
        unitPrice: 14999.99,
        unitsInStock: 20,
        unitsOnOrder: 10,
        reorderLevel: 5,
        discontinued: false,
        categoryID: 1
    });

    const headers = {
        'Authorization': `Bearer ${data.token}`,
        'Content-Type': 'application/json'
    };

    const start = Date.now();
    const res = http.post('http://localhost:8080/api/products', payload, { headers });
    const elapsed = Date.now() - start;
    createProductTime.add(elapsed);

    check(res, {
        'status is 202': (r) => r.status === 202
    });
}
