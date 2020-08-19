import http from 'k6/http';
import {check, sleep} from 'k6';

// k6 run --vus 10 --duration 30s perf.js

export default function () {
    let data = {data: {}};
    http.post('http://demo-proxy:8080/tests', JSON.stringify(data));
    // http.get('http://127.0.0.1:8080/tests');
    sleep(1);
}
