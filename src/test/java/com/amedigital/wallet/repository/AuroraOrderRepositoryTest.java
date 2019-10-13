//package com.amedigital.wallet.repository;
//
//import com.amedigital.wallet.constants.enuns.*;
//import com.amedigital.wallet.model.*;
//import com.amedigital.wallet.model.order.Action;
//import com.amedigital.wallet.model.order.Order;
//import com.amedigital.wallet.model.order.primary.PurchaseOrder;
//import com.amedigital.wallet.model.transaction.CashTransaction;
//import com.amedigital.wallet.model.transaction.CreditCardTransaction;
//import com.amedigital.wallet.model.transaction.Transaction;
//import com.amedigital.wallet.repository.impl.*;
//import com.amedigital.wallet.repository.mappers.OrderMapper;
//import com.amedigital.wallet.repository.mappers.OwnerMapper;
//import com.amedigital.wallet.repository.mappers.TransactionMapper;
//import com.amedigital.wallet.repository.mappers.WalletMapper;
//import org.jdbi.v3.core.Jdbi;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import reactor.core.scheduler.Scheduler;
//import reactor.core.scheduler.Schedulers;
//
//import java.time.ZonedDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//import java.util.UUID;
//import java.util.concurrent.Executors;
//
//public class AuroraOrderRepositoryTest {
//
//    private static AuroraOrderRepository repository;
//    private static WalletRepositoryImpl walletRepository;
//    private final Random random = new Random();
//
//    private String generateCPF() {
//        return random.nextInt(999)+""+random.nextInt(999)+""+random.nextInt(999)+""+random.nextInt(99);
//    }
//
//    @BeforeAll
//    static void setUpAll() {
//        Jdbi jdbi = Jdbi.create("jdbc:mysql://127.0.0.1/db_wallet"
//                , "wallet"
//                , "wallet");
//        jdbi.registerRowMapper(new WalletMapper());
//        jdbi.registerRowMapper(new OwnerMapper());
//        jdbi.registerRowMapper(new OrderMapper());
//        jdbi.registerRowMapper(new TransactionMapper());
//
//        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(1));
//
//        walletRepository =
//                new WalletRepositoryImpl(jdbi, scheduler, new OwnerRepositoryImpl(jdbi, scheduler));
//
//        Scheduler jdbcScheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(2));
//        repository = new AuroraOrderRepository(jdbi, new AuroraActionRepository(), new AuroraTransactionRepository(jdbi), jdbcScheduler);
//    }
//
//    @DisplayName("Dado o uuid de uma ordem deve trazer a mais recente.")
//    @Test
//    void teste2() {
//
//        Order order = repository.findByUuid("563bf0bf-193a-4f60-bfe8-3d5666e9f867").block();
//
//        System.out.println(order);
//    }
//
//    @Test
//    @DisplayName("dado uma ordem de compra com cartao de credito no status CREATED deve persistir na base com sucesso.")
//    void teste01() {
//        Wallet wallet = Wallet.builder()
//                .setUuid(UUID.randomUUID().toString())
//                .setType(WalletType.CUSTOMER)
//                .setName("Teste")
//                .setMain(Boolean.TRUE)
//                .setCreatedAt(ZonedDateTime.now())
//                .setUpdatedAt(ZonedDateTime.now())
//                .setOwner(Owner.builder()
//                        .setUuid(UUID.randomUUID().toString())
//                        .setCreatedAt(ZonedDateTime.now())
//                        .setDocumentType(DocumentType.CPF)
//                        .setEmail(UUID.randomUUID().toString()+"@hotmail.com")
//                        .setName("Mauricio")
//                        .setExternalId(UUID.randomUUID().toString())
//                        .setDocument(generateCPF())
//                        .build())
//                .build();
//
//
//        Wallet insertedWallet = walletRepository.insert(wallet).block();
//
//        Action action = new Action.Builder()
//                .setType(ActionType.CREATE)
//                .setCreatedAt(ZonedDateTime.now())
//                .build();
//
//        CreditCardTransaction creditCardTransaction = new CreditCardTransaction.Builder()
//                .setAmountInCents(100L)
//                .setStatus(TransactionStatus.CREATED)
//                .setCreditCardStatus(CreditCardStatus.CREATED)
//                .setType(TransactionType.DEBIT)
//                .setCreditCardId("asdfasdf")
//                .setUuid(UUID.randomUUID().toString())
//                .setCreatedAt(ZonedDateTime.now())
//                .setGatewayOrderReference(UUID.randomUUID().toString())
//                .setGatewayPaymentReference(UUID.randomUUID().toString())
//                .setUpdatedAt(ZonedDateTime.now())
//                .setWalletId(insertedWallet.getId().get())
//                .build();
//
//        CashTransaction cashTransaction = new CashTransaction.Builder()
//                .setCreatedAt(ZonedDateTime.now())
//                .setStatus(TransactionStatus.CREATED)
//                .setCashStatus(CashStatus.CREATED)
//                .setType(TransactionType.DEBIT)
//                .setAmountInCents(200L)
//                .setUuid(UUID.randomUUID().toString())
//                .setWalletId(1L)
//                .setUpdatedAt(ZonedDateTime.now())
//                .setCreatedAt(ZonedDateTime.now())
//                .setCashCreatedAt(ZonedDateTime.now())
//                .setCashUpdatedAt(ZonedDateTime.now())
//                .build();
//
//        List<Transaction> transactions = Arrays.asList(creditCardTransaction, cashTransaction);
//        PurchaseOrder purchaseOrder = new PurchaseOrder.Builder()
//                .setUuid(UUID.randomUUID().toString())
//                .setAction(action)
//                .setTotalAmountInCents(100L)
//                .setAuthorizationMethod(AuthorizationMethod.QRCODE)
//                .setCreatedByWalletId(insertedWallet.getId().get())
//                .setTitle("Titulo da ordem")
//                .setDescription("Descrição da ordem")
//                .setOrderDetailUuid(UUID.randomUUID().toString())
//                .setStatus(OrderStatus.CREATED)
//                .setTransactions(transactions)
//                .setCreatedAt(ZonedDateTime.now())
//                .build();
//
//
//        Order monoInsert = repository.save(purchaseOrder).block();
//
//        System.out.println(monoInsert);
//    }
//}