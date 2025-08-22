/**

1. pg-simulator 찌를 feign-client 어따 둬요?

- /infrastructure 다... 왜? outbound 시스템이니까.
  outbound : 시작점이 나가는 것
  inbound : 시작점이 들어오는 것

/interfaces -> inbound
  /api
    /payment
PaymentController.java

- 결제요청 API
- 결제콜백 API

/infrastructure -> outbound
/http
PgFeignClient.java

2. 결제 수단에 따라 처리가 다른데요..?

- 결제처리를 할 때
  (1) 포인트 결제인 경우 - 동기
  -> 결제 생성과 같은 트랜잭션에 묶여있으면 유리하다.
  -> 대신 포인트가 부족해서 결제를 실패했다 라는 것은 알아야하기 때문에.
  -> 롤백 시키는 게 아니라 Payment(status = FAILURE) 인 애를 만들어야 해...
  (2) 카드 결제인 경우 - 비동기
  -> 결제 생성과 같은 트랜잭션에 묶여있으면 안된다.
  -> DB 트랜잭션 <> API 요청은 같은 스코프 내에 있으면 안된다.
  -> 결제 요청이 성공했으면, 그 후에 추적이 가능해야하기 때문에 우리쪽 증적으로 Payment 가 남아야 한다.
  */

/*

*/

// @Transactional
주문 파사드 {
orderApplicationService.placeOrder() // 주문 처리
주문처리() // @Transactional 로 묶여있음

    paymentProcessorRegistry.process() // 적절한 전략 객체 ㄱ- 씀
    결제요청() // -> @Transactional 쓸놈 써. 안쓸놈 쓰지마.

}

interface PaymentProcessor { } // 전략패턴

class PaymentApplicationService(
private val paymentRepository: PaymentRepository,
) {
companion object {
private val logger = LoggerFactory.getLogger(PaymentApplicationService::class.java)
}
// 결제 요청
fun requestPayment(order: PaymentOrder) {
order 타입이 카드면 -> CardProcessor {
try {
val result = paymentRepository.requestTransaction(order) // circuit 달구... timeout 걸구..
// timeout 이 나버리면.. 뒤에선 성공했을 수도 있음. 우리는 충분히 기다렸다고 생각하나... 느~~리게 결제가 되버린거지...
val transactionKey = result.transactionKey
// create Payment

                } catch (e: Throwable) {
                    logger.error("주문 ID = ${order.orderId} 에 해당하는 주문이 실패했습니다. {}", e.message, e)
                }
            }
            order 타입이 포인트면 -> PointProcessor @Transactional {
                Payment()
                try {
                    pointRepository.getPoint()
                    point.use()
                    payment.status = SUCCESS
                } catch {
                    payment.status = FAILURE
                }
            }
    }

    // 결제 상태 동기화
    // callback, 상태 조회든 다 여기에 속하는 것..
    fun syncPayment() {
        // callback 받았을 때, 스케줄러가 호출할 기능
        // -> payment 존재
        // -> 상태 API 요청해서 진짜인지 확인
        // ---- 동기화 안정성은 챙김
        // ++ orderId 에 따른 transaction 목록 조회해서 검증 (결제 성공건이 여러개 있으면 안됨)
        // 1. 주문이 결제완료?
        // 2. 주문이 아직 결제미결?
        // 3. 해당하는 transaction 을 추적하는 Payment 가 있냐 없냐..

        /**
         * 성공으로 왔을 경우
         * - 성공 마킹
         * - 주문을 이제 진행시킴 ( 결제 완료 뒷 스텝으로 )
         *
         * 실패로 왔을 경우
         * - 실패 마킹
         *
         */
    }

}

data class PaymentOrder(
val orderId: String,
val amount: String,
val cardNo: String,
val cardType: String,
val callbackUrl: String,
)

data class PaymentTransaction(
val transactionKey: String,
val orderId: String,
val cardType: String,
val cardNo: String,
val amount: Long,
val status: String,
val reason: String?,
)

class Payment(
val orderId: String,
val transactionKey: String, // PK 가 되어도 됨.
var status: String,
)

data class PaymentOrderResult(val transactionKey: String)

interface PaymentRepository {
fun findPayment()

    fun findTransaction(transactionId: String): PaymentTransaction
    fun requestTransaction(order: PaymentOrder): PaymentOrderResult

}