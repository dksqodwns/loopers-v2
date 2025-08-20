package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_PRODUCT_ID = 10L;
    private static final Long TEST_PRICE = 1000L;
    private static final Integer TEST_QUANTITY = 2;

    private OrderItem createOrderItem() {
        return new OrderItem(TEST_PRODUCT_ID, TEST_PRICE, TEST_QUANTITY);
    }

    @Nested
    @DisplayName("Order 생성 시")
    class Create {

        @Test
        @DisplayName("userId가 null이면 CoreException(BAD_REQUEST)을 던진다")
        void throwCoreException_whenUserIdIsNull() {
            CoreException exception = assertThrows(CoreException.class, () ->
                    new Order(null, List.of(createOrderItem()))
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("orderItems가 null이면 CoreException(BAD_REQUEST)을 던진다")
        void throwCoreException_whenOrderItemsIsNull() {
            CoreException exception = assertThrows(CoreException.class, () ->
                    new Order(TEST_USER_ID, null)
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("orderItems가 비어있으면 CoreException(BAD_REQUEST)을 던진다")
        void throwCoreException_whenOrderItemsIsEmpty() {
            CoreException exception = assertThrows(CoreException.class, () ->
                    new Order(TEST_USER_ID, new ArrayList<>())
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("모든 값이 유효하면 Order 객체가 올바르게 생성된다")
        void createOrderSuccessfully_whenAllValuesAreValid() {
            List<OrderItem> items = List.of(createOrderItem());
            Order order = new Order(TEST_USER_ID, items);

            assertAll(
                    () -> assertThat(order.getUserId()).isEqualTo(TEST_USER_ID),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING),
                    () -> assertThat(order.getItems()).hasSize(1),
                    () -> assertThat(order.getItems().get(0).getProductId()).isEqualTo(TEST_PRODUCT_ID)
            );
        }
    }

    @Nested
    @DisplayName("addOrderItem 메서드는")
    class AddOrderItem {

        @Test
        @DisplayName("OrderItem을 추가하면 items 리스트에 포함된다")
        void addOrderItem_addsItemToList() {
            Order order = new Order(TEST_USER_ID, List.of(createOrderItem()));
            OrderItem newItem = new OrderItem(TEST_PRODUCT_ID + 1, TEST_PRICE, TEST_QUANTITY);
            order.addOrderItem(newItem);

            assertThat(order.getItems()).hasSize(2);
            assertThat(order.getItems()).contains(newItem);
        }
    }

    @Nested
    @DisplayName("calculateTotalPrice 메서드는")
    class CalculateTotalPrice {

        @Test
        @DisplayName("모든 OrderItem의 총 가격을 올바르게 계산한다")
        void calculateTotalPrice_correctlyCalculatesTotal() {
            OrderItem item1 = new OrderItem(TEST_PRODUCT_ID, 1000L, 2);
            OrderItem item2 = new OrderItem(TEST_PRODUCT_ID + 1, 500L, 3);
            List<OrderItem> items = List.of(item1, item2);
            Order order = new Order(TEST_USER_ID, items);

            assertThat(order.calculateTotalPrice()).isEqualTo(1000L * 2 + 500L * 3);
        }
    }

    @Nested
    @DisplayName("cancel 메서드는")
    class Cancel {

        @Test
        @DisplayName("호출 시 Order 상태를 CANCELED로 변경한다")
        void changeStatusToCanceled_whenCalled() {
            Order order = new Order(TEST_USER_ID, List.of(createOrderItem()));
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        }
    }

    @Nested
    @DisplayName("confirm 메서드는")
    class Confirm {

        @Test
        @DisplayName("호출 시 Order 상태를 CONFIRM으로 변경한다")
        void changeStatusToConfirm_whenCalled() {
            Order order = new Order(TEST_USER_ID, List.of(createOrderItem()));
            order.confirm();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRM);
        }
    }

    @Nested
    @DisplayName("complete 메서드는")
    class Complete {

        @Test
        @DisplayName("호출 시 Order 상태를 COMPLETED로 변경한다")
        void changeStatusToCompleted_whenCalled() {
            Order order = new Order(TEST_USER_ID, List.of(createOrderItem()));
            order.complete();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }
}
