# 결제 시스템 연동 계획

## 1. 핵심 목표

1.  **안정성 (Resilience)**: 외부 결제 시스템(PG)의 장애가 우리 서비스 전체의 장애로 확산되지 않도록 격리하고, 다양한 실패 상황에 대응할 수 있어야 한다.
2.  **데이터 정합성 (Consistency)**: 네트워크 오류, 타임아웃, 콜백 유실 등 어떤 상황에서도 최종적으로 주문과 결제 상태는 올바르게 기록되어야 한다.
3.  **유지보수성 (Maintainability)**: DDD 원칙에 따라 계층별 역할과 책임을 명확히 분리하여, 코드를 이해하고 수정하기 쉬운 구조를 가진다.

---

## 2. 전체 프로세스 흐름

### 1단계: 결제 시도 (Payment Attempt)

1.  **사용자**: 카드 정보를 포함하여 '결제하기'를 요청한다.
2.  **시스템 (Application Layer)**:
    -   데이터베이스 트랜잭션을 시작한다.
    -   `Order`를 `PENDING` 상태로 생성한다.
    -   `Payment`를 `REQUESTED` 상태로 생성한다. (결제 시도 기록)
    -   두 객체를 DB에 저장한다.
3.  **시스템 (Infrastructure Layer)**:
    -   `PaymentGatewayPort`를 통해 PG에 결제 요청을 보낸다. (Resilience 정책 적용: Retry, Circuit Breaker)
4.  **응답 처리**:
    -   **성공 시**: PG로부터 `transactionKey`를 즉시 응답받는다. 이 `transactionKey`를 `Payment` 레코드에 업데이트하고 DB 트랜잭션을 커밋한다.
    -   **실패 시**: PG로부터 즉각적인 실패(예: 한도 초과)를 응답받는다. `Order`와 `Payment` 상태를 `FAILED`로 업데이트하고 DB 트랜잭션을 커밋한다.
5.  **사용자 피드백**:
    -   요청이 성공적으로 전달되면, 사용자에게 "결제 진행 중..." 페이지를 보여준다.
    -   즉시 실패하면, 사용자에게 실패 사유를 알리고 재시도를 유도한다.

### 2단계: 비동기 결과 처리 (Asynchronous Result Processing)

1.  **PG**: 내부적으로 카드사 승인/거절 처리를 완료한다. (수 초 ~ 수 분 소요 가능)
2.  **콜백(Callback)**: 처리가 완료되면, PG는 우리 시스템의 지정된 `callbackUrl`로 최종 결제 결과를 POST 요청으로 보낸다.
3.  **시스템 (Controller & Application Layer)**:
    -   `PaymentCallbackController`가 콜백 요청을 받는다.
    -   `PaymentService`는 콜백 데이터에 포함된 `transactionKey`를 이용해 `Payment`와 `Order`를 조회한다.
    -   콜백 결과(`COMPLETED` 또는 `FAILED`)에 맞춰 `Payment`와 `Order`의 상태를 업데이트하고 DB에 저장한다.

### 3단계: 최종 상태 동기화 (Final Status Synchronization) - 안전망

1.  **스케줄러 실행**: `PaymentStatusScheduler`가 1분마다 주기적으로 실행된다.
2.  **처리 지연 건 확인**: DB에서 생성된 지 5분이 지났지만 여전히 `REQUESTED` 상태인 `Payment`를 모두 찾는다.
3.  **상태 재확인**:
    -   해당 `Payment`의 `transactionKey`를 가지고 PG의 **"결제 정보 확인 API"** 를 직접 호출한다.
    -   PG에 기록된 **'진짜' 최종 상태**를 응답받는다.
4.  **상태 복구 (Self-Healing)**: 조회된 최종 상태를 우리 시스템의 `Payment`와 `Order`에 반영하여 데이터 정합성을 맞춘다.

---

## 3. 주요 장애 시나리오 및 대응 전략

| 시나리오 | 문제 상황 | 해결 전략 |
| :--- | :--- | :--- |
| **A. PG 즉시 실패** | PG가 요청 시점에 "한도 초과" 등 명확한 실패를 반환 | `PaymentService`가 즉시 `Order`와 `Payment` 상태를 `FAILED`로 처리하고 사용자에게 알림 |
| **B. PG 응답 지연 / 타임아웃** | PG에 요청했으나 응답이 오지 않음. 돈이 결제되었는지 알 수 없음 | **`PaymentStatusScheduler`** 가 `REQUESTED` 상태인 결제를 감지하고, "결제 정보 확인 API"를 통해 최종 상태를 조회하여 DB에 반영 |
| **C. PG 콜백 유실** | PG는 결제를 완료했으나, 우리 서버로 보내는 콜백이 네트워크 문제로 유실됨 | **`PaymentStatusScheduler`** 가 `REQUESTED` 상태인 결제를 감지하고, "결제 정보 확인 API"를 통해 최종 상태를 조회하여 DB에 반영 (B와 동일) |
| **D. PG 시스템 전체 장애** | PG의 모든 API가 실패를 반환하거나 응답이 없음 | **`Circuit Breaker`** 가 발동하여 PG로 가는 모든 요청을 즉시 차단하고 실패 처리함. 우리 시스템으로 장애가 전파되는 것을 막음 |

---

## 4. API 명세 추정

### 콜백(Callback) 데이터 구조

`README.md`에 명시되어 있지 않지만, "결제 정보 확인 API"의 응답과 동일한 구조일 것으로 가정한다.

-   **예상 JSON Body**:
    ```json
    {
      "transactionKey": "20250816:TR:9577c5",
      "orderId": "1351039135",
      "status": "COMPLETED",
      "amount": "5000"
    }
    ```
-   **대응 DTO**: `infrastructure.payment.client.PgPaymentResponse`