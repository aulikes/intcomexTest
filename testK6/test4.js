import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';
import { SharedArray } from 'k6/data';

// Prepara 1000 usuarios distintos (¡puedes ajustar la cantidad fácilmente!)
const users = new SharedArray('users', function () {
    const arr = [];
    for (let i = 0; i < 1000; i++) {
        arr.push({
            username: `usuario${i + 1}`,
            password: `password${i + 1}`
        });
    }
    return arr;
});

const createProductTime = new Trend('create_product_time');

export const options = {
    scenarios: {
        burst_1000_rps: {
            executor: 'constant-arrival-rate',
            rate: 1000,             // 1000 peticiones por segundo
            timeUnit: '1s',
            duration: '1s',         // solo 1 segundo
            preAllocatedVUs: 1000,  // ajusta según la rapidez de tu máquina
            maxVUs: 2000,
        }
    },
    thresholds: {
        'checks{status is 202}': ['rate>0.99'],    // Falla si más del 1% fallan
        'create_product_time': ['avg<500'],        // Falla si el promedio supera 500ms
    }
};

export default function () {
    // Cada iteración usa un usuario distinto (puedes hacer aleatorio o secuencial)
    const idx = (__ITER % users.length);
    const user = users[idx];

    // LOGIN: cada request usa su propio usuario
    const loginRes = http.post('http://localhost:8080/auth/login', JSON.stringify({
        username: user.username,
        password: user.password
    }), {
        headers: {
            'Content-Type': 'application/json'
        }
    });

    const token = loginRes.json('token');

    // Datos del producto (puedes parametrizar más si quieres)
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
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };

    // Medir tiempo solo de la petición de creación
    const start = Date.now();
    const res = http.post('http://localhost:8080/api/products', payload, { headers });
    const elapsed = Date.now() - start;
    createProductTime.add(elapsed);

    check(res, {
        'status is 202': (r) => r.status === 202
    });
}
