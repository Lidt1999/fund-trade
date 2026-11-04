# fund-trade

A Spring Cloud Alibaba-based fund distribution transaction platform.

## Development stages

- **Stage 0 (complete):** Maven multi-module project skeleton and independently startable service applications.
- **Stage 1 (complete):** MySQL database plus asset-service CRUD for positions and pending assets.
- **Stage 2 (complete):** Trade service purchase acceptance. `POST /api/trades/purchases` creates an idempotent purchase order and records a `PENDING_CONFIRMATION` pending asset through asset-service.
- **Stage 3 (complete):** Trade service redemption acceptance. `POST /api/trades/redemptions` atomically freezes the requested available position shares in asset-service and creates an idempotent `PENDING_CONFIRMATION` redemption order.
- **Stage 4 (complete):** Trade service T+1 confirmation and end-of-day reconciliation. The `dailyReconciliationJobHandler` compares both purchase and redemption orders with TA records in both directions, persists reports and discrepancy details, and exposes `/api/reconciliations`.
- **Stage 5 (complete):** Gateway routing, CORS, and configurable request authentication. The gateway exposes port `8080` and routes `/api/trades/**`, `/api/assets/**`, and `/api/payments/**` through service discovery.
- **Stage 6 (complete):** Sentinel gateway flow control protects purchase traffic at 1000 requests/second and redemption traffic at 800 requests/second; the TA confirmation job uses a Sentinel circuit breaker and skips processing on failures so pending orders can be retried.
- **Stage 7 (complete):** AOP audit logging records operation, caller, outcome, latency, and request endpoint for transaction and reconciliation submissions.

## Database setup

Install and start MySQL 8+, then create the two service databases:

```sql
CREATE DATABASE fund_trade CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE fund_asset CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE fund_payment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The default connection is `localhost:3306` with user `root` and an empty password. Configure a different environment with `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_USERNAME`, `MYSQL_PASSWORD`, `MYSQL_TRADE_DATABASE`, and `MYSQL_ASSET_DATABASE`. Apply `sql/schema.sql` before starting the services; Hibernate validates the existing tables at startup (`ddl-auto: validate`).

## Payment service

Start the service with `./mvnw.cmd -pl fund-payment-service -am spring-boot:run`. It uses the `fund_payment` database and provides simulated account operations:

- `POST /api/payments/accounts` creates a user account with an initial balance
- `GET /api/payments/accounts` lists accounts
- `POST /api/payments/freeze` freezes available funds
- `POST /api/payments/debit` debits frozen funds
- `POST /api/payments/release` releases frozen funds back to the available balance
- `POST /api/payments/credit` credits funds to an account

Payment operations require a unique `requestId`; repeating a request is idempotent.

## Nacos

The trade, asset, payment, and gateway applications register with Nacos and can load optional configuration from `${spring.application.name}.yaml` in `DEFAULT_GROUP`. Start Nacos at `localhost:8848`, or override the address with `NACOS_SERVER_ADDR`.

## Gateway

Start the unified entry point with `./mvnw.cmd -pl fund-gateway -am spring-boot:run`. Requests require a non-empty `Authorization` header by default; set `GATEWAY_AUTH_ENABLED=false` for local unauthenticated development. The gateway forwards requests through Nacos load balancing.

Sentinel limits `POST /api/trades/purchases` to 1000 requests/second and `POST /api/trades/redemptions` to 800 requests/second.

The trade service protects `dailyConfirmJobHandler` with a Sentinel error-count circuit breaker. One failure within the 10-second statistics window opens the circuit for 30 seconds. While open, or when TA confirmation processing throws an exception, the current run is skipped and orders remain pending for a later retry. TA confirmation is simulated in this project; this circuit protects the existing confirmation-job boundary.

## Asset service CRUD

Start the service with `./mvnw.cmd -pl fund-asset-service -am spring-boot:run`.

- `GET/POST /api/assets/positions`, `GET/PUT/DELETE /api/assets/positions/{id}`
- `GET/POST /api/assets/pending`, `GET/PUT/DELETE /api/assets/pending/{id}`

The asset and trade services each own a separate MySQL database. Purchase business is deliberately deferred to Stage 2.

## Purchase acceptance

Start asset-service first, then trade-service. Submit `POST /api/trades/purchases` to port `8081`:

```json
{"clientRequestId":"request-001","userId":1001,"productCode":"FUND001","shares":200}
```

`clientRequestId` is the idempotency key: repeating the same request returns the original order instead of creating another pending asset.

## Redemption acceptance

Create a position in asset-service first, then submit `POST /api/trades/redemptions` to port `8081`:

```json
{"clientRequestId":"redeem-001","userId":1001,"productCode":"FUND001","shares":200}
```

The asset service moves the shares from `shares` (available) to `frozenShares`; a request exceeding the available balance is rejected. Repeating a successful request with the same `clientRequestId` returns the original order without freezing shares again.
