import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

// métrica personalizada para medir solo la creación del producto
const createProductTime = new Trend('create_product_time');

export const options = {
    vus: 100,              // usuarios concurrentes
    duration: '1m',        // duración de la prueba
};

export function setup() {
    const headers = {
        'Content-Type': 'application/json;',
    };

    const loginRes = http.post('http://localhost:8080/auth/login', JSON.stringify({
        username: 'admin',
        password: 'admin123'
    }), { headers });

    const token = loginRes.json('token');
    return { token };
}

export default function (data) {
    // Parámetros aleatorios para cada request
    const payload = JSON.stringify({
        productName: 'Servidor Pro X500 ' + Math.random().toString(36).substring(2, 10),
        quantityPerUnit: '1 unidad',
        unitPrice: Math.floor(Math.random() * 20000) + 100,
        unitsInStock: Math.floor(Math.random() * 100),
        unitsOnOrder: Math.floor(Math.random() * 10),
        reorderLevel: Math.floor(Math.random() * 5),
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