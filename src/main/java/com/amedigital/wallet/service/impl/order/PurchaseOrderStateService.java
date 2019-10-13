package com.amedigital.wallet.service.impl.order;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.exceptions.AmeNotFoundException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.OrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.purchase.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amedigital.wallet.constants.enuns.OrderStatus.*;
import static com.amedigital.wallet.util.ValidatorUtil.notEmpty;
import static com.amedigital.wallet.util.ValidatorUtil.notNull;

@Service
public class PurchaseOrderStateService implements OrderStateService<PurchaseOrder> {

    private final Map<OrderStatus, OrderState> states = new HashMap<>();
    private final WalletRepository walletRepository;

    @Autowired
    public PurchaseOrderStateService(CreatedPurchaseOrderState createdOrderState,
                                     AuthorizedPurchaseOrderState authorizedPurchaseOrderState,
                                     DeniedPurchaseOrderState deniedPurchaseOrderState,
                                     CapturedPurchaseOrderState capturedPurchaseOrderState,
                                     CancelledPurchaseOrderState cancelledPurchaseOrderState,
                                     WalletRepository walletRepository) {

        states.put(CREATED, createdOrderState);
        states.put(AUTHORIZED, authorizedPurchaseOrderState);
        states.put(DENIED, deniedPurchaseOrderState);
        states.put(CAPTURED, capturedPurchaseOrderState);
        states.put(CANCELLED, cancelledPurchaseOrderState);

        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<PurchaseOrder> create(PurchaseOrder purchaseOrder) {
        return validateFields(purchaseOrder)
                .then(walletRepository.findByUuid(purchaseOrder.getCreatedByWalletUuid()))
                .flatMap(w ->
                        states.get(purchaseOrder.getStatus())
                                .create(purchaseOrder.copy()
                                .setCreatedByWalletId(w.getId().get())
                                .build()))
                .switchIfEmpty(
                        Mono.error(new AmeNotFoundException("wallet_validation",
                                "Informações não encontradas do tipo MERCHANT da walletId " + purchaseOrder.getCreatedByWalletUuid())));
    }

    @Override
    public Mono<PurchaseOrder> capture(PurchaseOrder purchaseOrder) {
        return states.get(purchaseOrder.getStatus()).capture(purchaseOrder);
    }

    @Override
    public Mono<PurchaseOrder> authorize(PurchaseOrder purchaseOrder) {
        return states.get(purchaseOrder.getStatus()).authorize(purchaseOrder);
    }

    @Override
    public Mono<PurchaseOrder> cancel(PurchaseOrder purchaseOrder) {
        return states.get(purchaseOrder.getStatus()).cancel(purchaseOrder);
    }

    private Mono<Void> validateFields(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getTotalAmountInCents() <= 0) {
            throw new AmeInvalidInputException("wallet_validation", "O valor da ordem deve ser maior que 0.");
        }

        List<Transaction> transactions = purchaseOrder.getTransactions();

        notEmpty(purchaseOrder.getTitle(), "título da ordem");
        notNull(transactions, "métodos de pagamento");

        if (transactions.stream().anyMatch(t -> t.getAmountInCents() <= 0)) {
            throw new AmeInvalidInputException("wallet_validation",
                    "Uma paymentMethod não pode ter o valor menor ou igual a 0.");
        }

        var total = transactions
                .stream()
                .mapToLong(Transaction::getAmountInCents)
                .sum();

        if (purchaseOrder.getTotalAmountInCents() != total) {
            throw new AmeInvalidInputException("wallet_validation",
                    "A soma dos meios de pagamento deve ser igual ao valor total da compra.");
        }

        return Mono.empty();
    }
}
