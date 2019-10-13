package com.amedigital.wallet.service.state.order.purchase;

import com.amedigital.wallet.commons.Constants;
import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.transaction.CashBackTransaction;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.DynamoRepository;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.strategy.BalanceRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.amedigital.wallet.constants.enuns.TransactionStatus.AUTHORIZED;
import static com.amedigital.wallet.constants.enuns.TransactionStatus.DENIED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CreatedPurchaseOrderStateTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private PaymentMethodRouter paymentMethodRouter;

    @Mock
    private DynamoRepository dynamoRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private BalanceRouter router;

    @InjectMocks
    private CreatedPurchaseOrderState purchaseOrderState;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    class AuthorizedWithCashBack {

        @Test
        @DisplayName("Deve autorizar uma ordem com a forma de pagamento CASH_BACK.")
        void test1() {
            var cashBackTransaction = buildCashBackTransaction();
            var createdPurchaseOrder = buildPurchaseOrder(Collections.singletonList(cashBackTransaction));

            when(router.route(any(PaymentMethod.class), any(Order.class))).thenReturn(Mono.empty());

            when(paymentMethodRouter.authorize(any(Transaction.class)))
                    .thenReturn(Mono.just(changeStatusCashBackTransaction(cashBackTransaction, CashBackStatus.AUTHORIZED, AUTHORIZED)));

            when(repository.save(any(Order.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));


            var authorizedPurchaseOrder = purchaseOrderState.authorize(createdPurchaseOrder).block();

            assertNotNull(authorizedPurchaseOrder);
            assertEquals(OrderStatus.AUTHORIZED, authorizedPurchaseOrder.getStatus());

            assertEquals(ActionType.AUTHORIZE, authorizedPurchaseOrder.getAction().getType());

            var authorizedCashBackTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH_BACK == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashBackTransaction.class::cast)
                    .get();

            assertNotNull(authorizedCashBackTransaction);

            assertEquals(CashBackStatus.AUTHORIZED, authorizedCashBackTransaction.getCashStatus());
            assertEquals(AUTHORIZED, authorizedCashBackTransaction.getStatus());
        }

        @Test
        @DisplayName("Deve autorizar uma ordem com as formas de pagamento CASH e CASH_BACK.")
        void test2() {
            var cashBackTransaction = buildCashBackTransaction();
            var cashTransaction = buildCashTransaction();

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(cashBackTransaction);
            transactions.add(cashTransaction);

            when(router.route(any(PaymentMethod.class), any(Order.class))).thenReturn(Mono.empty());

            when(paymentMethodRouter.authorize(any(CashBackTransaction.class)))
                    .thenReturn(Mono.just(changeStatusCashBackTransaction(cashBackTransaction, CashBackStatus.AUTHORIZED, AUTHORIZED)));

            when(paymentMethodRouter.authorize(any(CashTransaction.class)))
                    .thenReturn(Mono.just(changeStatusCashTransaction(cashTransaction, CashStatus.AUTHORIZED, AUTHORIZED)));

            when(repository.save(any(Order.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var authorizedPurchaseOrder = purchaseOrderState.authorize(buildPurchaseOrder(transactions)).block();

            assertNotNull(authorizedPurchaseOrder);
            assertEquals(OrderStatus.AUTHORIZED, authorizedPurchaseOrder.getStatus());

            assertEquals(ActionType.AUTHORIZE, authorizedPurchaseOrder.getAction().getType());

            var authorizedCashBackTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH_BACK == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashBackTransaction.class::cast)
                    .get();

            var authorizedCashTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashTransaction.class::cast)
                    .get();

            assertNotNull(authorizedCashBackTransaction);

            assertEquals(CashBackStatus.AUTHORIZED, authorizedCashBackTransaction.getCashStatus());
            assertEquals(AUTHORIZED, authorizedCashBackTransaction.getStatus());

            assertNotNull(authorizedCashTransaction);

            assertEquals(CashStatus.AUTHORIZED, authorizedCashTransaction.getCashStatus());
            assertEquals(AUTHORIZED, authorizedCashTransaction.getStatus());
        }

        @Test
        @DisplayName("Deve autorizar uma ordem com as formas de pagamento CASH, CASH_BACK e CREDIT_CARD")
        void test3() {
            var cashBackTransaction = buildCashBackTransaction();
            var cashTransaction = buildCashTransaction();
            var creditCardTransaction = buildCreditCardTransaction();

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(cashBackTransaction);
            transactions.add(cashTransaction);
            transactions.add(creditCardTransaction);

            when(router.route(any(PaymentMethod.class), any(Order.class))).thenReturn(Mono.empty());

            when(paymentMethodRouter.authorize(any(CashBackTransaction.class)))
                    .thenReturn(Mono.just(changeStatusCashBackTransaction(cashBackTransaction, CashBackStatus.AUTHORIZED, AUTHORIZED)));

            when(paymentMethodRouter.authorize(any(CashTransaction.class)))
                    .thenReturn(Mono.just(changeStatusCashTransaction(cashTransaction, CashStatus.AUTHORIZED, AUTHORIZED)));


            when(paymentMethodRouter.authorize(any(CreditCardTransaction.class)))
                    .thenReturn(Mono.just(changeStatusCreditCardTransaction(creditCardTransaction, CreditCardStatus.AUTHORIZED, AUTHORIZED)));

            when(repository.save(any(Order.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var authorizedPurchaseOrder = purchaseOrderState.authorize(buildPurchaseOrder(transactions)).block();

            assertNotNull(authorizedPurchaseOrder);
            assertEquals(OrderStatus.AUTHORIZED, authorizedPurchaseOrder.getStatus());

            assertEquals(ActionType.AUTHORIZE, authorizedPurchaseOrder.getAction().getType());

            var authorizedCashBackTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH_BACK == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashBackTransaction.class::cast)
                    .get();

            var authorizedCashTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashTransaction.class::cast)
                    .get();

            var authorizedCreditCardTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CREDIT_CARD == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CreditCardTransaction.class::cast)
                    .get();

            assertNotNull(authorizedCashBackTransaction);

            assertEquals(CashBackStatus.AUTHORIZED, authorizedCashBackTransaction.getCashStatus());
            assertEquals(AUTHORIZED, authorizedCashBackTransaction.getStatus());

            assertNotNull(authorizedCashTransaction);

            assertEquals(CashStatus.AUTHORIZED, authorizedCashTransaction.getCashStatus());
            assertEquals(AUTHORIZED, authorizedCashTransaction.getStatus());

            assertNotNull(authorizedCreditCardTransaction);

            assertEquals(CreditCardStatus.AUTHORIZED, authorizedCreditCardTransaction.getCreditCardStatus());
            assertEquals(AUTHORIZED, authorizedCreditCardTransaction.getStatus());
        }

        @Test
        @DisplayName("Deve negar uma ordem com a forma de pagamento CASH_BACK.")
        void test4() {
            var cashBackTransaction = buildCashBackTransaction();
            var createdPurchaseOrder = buildPurchaseOrder(Collections.singletonList(cashBackTransaction));

            when(router.route(any(PaymentMethod.class), any(Order.class)))
                    .thenReturn(Mono.just(changeStatusCashBackTransaction(cashBackTransaction, CashBackStatus.DENIED, DENIED)));

            when(repository.save(any(Order.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));


            var authorizedPurchaseOrder = purchaseOrderState.authorize(createdPurchaseOrder).block();

            assertNotNull(authorizedPurchaseOrder);
            assertEquals(OrderStatus.DENIED, authorizedPurchaseOrder.getStatus());

            assertEquals(ActionType.AUTHORIZE, authorizedPurchaseOrder.getAction().getType());

            var authorizedCashBackTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH_BACK == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashBackTransaction.class::cast)
                    .get();

            assertNotNull(authorizedCashBackTransaction);

            assertEquals(CashBackStatus.DENIED, authorizedCashBackTransaction.getCashStatus());
            assertEquals(DENIED, authorizedCashBackTransaction.getStatus());
        }

        @Test
        @DisplayName("Deve negar uma ordem com as formas de pagamento CASH e CASH_BACK.")
        void test5() {
            var cashBackTransaction = buildCashBackTransaction();
            var cashTransaction = buildCashTransaction();

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(cashBackTransaction);
            transactions.add(cashTransaction);

            when(router.route(eq(PaymentMethod.CASH_BACK), any(Order.class))).thenReturn(Mono.empty());

            when(router.route(eq(PaymentMethod.CASH), any(Order.class)))
                    .thenReturn(Mono.just(changeStatusCashTransaction(cashTransaction, CashStatus.DENIED, DENIED)));

            when(repository.save(any(Order.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var authorizedPurchaseOrder = purchaseOrderState.authorize(buildPurchaseOrder(transactions)).block();

            assertNotNull(authorizedPurchaseOrder);
            assertEquals(OrderStatus.DENIED, authorizedPurchaseOrder.getStatus());

            assertEquals(ActionType.AUTHORIZE, authorizedPurchaseOrder.getAction().getType());

            var authorizedCashBackTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH_BACK == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashBackTransaction.class::cast)
                    .get();

            var authorizedCashTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashTransaction.class::cast)
                    .get();

            assertNotNull(authorizedCashBackTransaction);

            assertEquals(CashBackStatus.DENIED, authorizedCashBackTransaction.getCashStatus());
            assertEquals(DENIED, authorizedCashBackTransaction.getStatus());

            assertNotNull(authorizedCashTransaction);

            assertEquals(CashStatus.DENIED, authorizedCashTransaction.getCashStatus());
            assertEquals(DENIED, authorizedCashTransaction.getStatus());
        }

        @Test
        @DisplayName("Deve negar uma ordem com as formas de pagamento CASH, CASH_BACK e CREDIT_CARD.")
        void test6() {
            var cashBackTransaction = buildCashBackTransaction();
            var cashTransaction = buildCashTransaction();
            var creditCardTransaction = buildCreditCardTransaction();

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(cashBackTransaction);
            transactions.add(cashTransaction);
            transactions.add(creditCardTransaction);

            when(router.route(eq(PaymentMethod.CASH_BACK), any(Order.class))).thenReturn(Mono.empty());

            when(router.route(eq(PaymentMethod.CASH), any(Order.class))).thenReturn(Mono.empty());

            when(router.route(eq(PaymentMethod.CREDIT_CARD), any(Order.class))).thenReturn(Mono.empty());

            when(paymentMethodRouter.authorize(any(CashBackTransaction.class)))
                    .thenReturn(Mono.just(changeStatusCashBackTransaction(cashBackTransaction, CashBackStatus.AUTHORIZED, AUTHORIZED)));

            when(paymentMethodRouter.authorize(any(CashTransaction.class)))
                    .thenReturn(Mono.just(changeStatusCashTransaction(cashTransaction, CashStatus.AUTHORIZED, AUTHORIZED)));


            when(paymentMethodRouter.authorize(any(CreditCardTransaction.class)))
                    .thenReturn(Mono.just(changeStatusCreditCardTransaction(creditCardTransaction, CreditCardStatus.UNAUTHORIZED, DENIED)));

            when(repository.save(any(Order.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var authorizedPurchaseOrder = purchaseOrderState.authorize(buildPurchaseOrder(transactions)).block();

            assertNotNull(authorizedPurchaseOrder);
            assertEquals(OrderStatus.DENIED, authorizedPurchaseOrder.getStatus());

            assertEquals(ActionType.AUTHORIZE, authorizedPurchaseOrder.getAction().getType());

            var authorizedCashBackTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH_BACK == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashBackTransaction.class::cast)
                    .get();

            var authorizedCashTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CashTransaction.class::cast)
                    .get();

            var authorizedCreditCardTransaction = authorizedPurchaseOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CREDIT_CARD == transaction.getPaymentMethod())
                    .findFirst()
                    .map(CreditCardTransaction.class::cast)
                    .get();


            assertNotNull(authorizedCashBackTransaction);

            assertEquals(CashBackStatus.DENIED, authorizedCashBackTransaction.getCashStatus());
            assertEquals(DENIED, authorizedCashBackTransaction.getStatus());

            assertNotNull(authorizedCashTransaction);

            assertEquals(CashStatus.DENIED, authorizedCashTransaction.getCashStatus());
            assertEquals(DENIED, authorizedCashTransaction.getStatus());

            assertNotNull(authorizedCreditCardTransaction);

            assertEquals(CreditCardStatus.UNAUTHORIZED, authorizedCreditCardTransaction.getCreditCardStatus());
            assertEquals(DENIED, authorizedCreditCardTransaction.getStatus());
        }

    }

    private CashTransaction changeStatusCashTransaction(CashTransaction transaction,
                                                        CashStatus status,
                                                        TransactionStatus transactionStatus) {
        var now = ZonedDateTime.now();

        return transaction
                .copy()
                .setUpdatedAt(now)
                .setCashStatus(status)
                .setCashUpdatedAt(now)
                .setStatus(transactionStatus)
                .build();
    }

    private CreditCardTransaction changeStatusCreditCardTransaction(CreditCardTransaction transaction,
                                                                    CreditCardStatus status,
                                                                    TransactionStatus transactionStatus) {
        var now = ZonedDateTime.now();

        return transaction.copy()
                .setUpdatedAt(now)
                .setCreditCardUpdatedAt(now)
                .setCreatedAt(now)
                .setCreditCardCreatedAt(now)
                .setCreditCardStatus(status)
                .setStatus(transactionStatus)
                .build();
    }

    private CreditCardTransaction buildCreditCardTransaction() {
        var now = ZonedDateTime.now();

        return new CreditCardTransaction.Builder()
                .setWalletId(2L)
                .setAmountInCents(10L)
                .setCreditCardStatus(CreditCardStatus.CREATED)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setCreditCardCreatedAt(now)
                .setCreditCardUpdatedAt(now)
                .build();
    }

    private CashTransaction buildCashTransaction() {
        var now = ZonedDateTime.now();

        return new CashTransaction.Builder()
                .setWalletId(2L)
                .setAmountInCents(10L)
                .setCashStatus(CashStatus.CREATED)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setCashCreatedAt(now)
                .setUpdatedAt(now)
                .build();
    }

    private CashBackTransaction changeStatusCashBackTransaction(CashBackTransaction transaction,
                                                                CashBackStatus status,
                                                                TransactionStatus transactionStatus) {
        var now = ZonedDateTime.now();

        return transaction
                .copy()
                .setUpdatedAt(now)
                .setCashStatus(status)
                .setCashUpdatedAt(now)
                .setStatus(transactionStatus)
                .build();
    }

    private CashBackTransaction buildCashBackTransaction() {
        var now = ZonedDateTime.now();

        return new CashBackTransaction.Builder()
                .setWalletId(2L)
                .setAmountInCents(10L)
                .setCashStatus(CashBackStatus.CREATED)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setCashCreatedAt(now)
                .setUpdatedAt(now)
                .build();
    }

    private PurchaseOrder buildPurchaseOrder(List<Transaction> transactions) {
        var now = ZonedDateTime.now();

        var action = new Action.Builder()
                .setId(1L)
                .setCreatedAt(now)
                .setType(ActionType.CREATE)
                .build();

        return new PurchaseOrder.Builder()
                .setId(0L)
                .setUuid(UUID.randomUUID().toString())
                .setType(OrderType.PURCHASE)
                .setStatus(OrderStatus.CREATED)
                .setTotalAmountInCents(10L)
                .setTitle(Constants.ORDER_TILE)
                .setDescription(Constants.ORDER_DESCRIPTION)
                .setAuthorizationMethod(AuthorizationMethod.QRCODE)
                .setCreatedByWalletId(1L)
                .setTransactions(transactions)
                .setCustomPayload(Collections.emptyMap())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setAction(action)
                .build();

    }

}
