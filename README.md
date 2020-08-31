# Demo gateway

Reproducer for memory leak in Spring Gateway.

Custom filter calling backend service. This call ends with 404, mapped to error.

Build:
```
./gradlew clean build
```

Run with docker:

```
docker-compose up  --build --abort-on-container-exit
```

It builds code, start backend service as Wiremock instance and runs pef tests with k6.


Check logs for Netty LEAK:
```
docker logs  demo-gateway_demo-proxy_1 -f | grep LEAK
```
