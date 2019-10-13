package com.amedigital.wallet.endoint.converter.order.secondary;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TakeRateUnit;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.request.order.PaymentMethodRuleRequest;
import com.amedigital.wallet.endoint.request.order.ReleaseOrderRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.secondary.PaymentMethodRule;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReleaseRequestConverter implements SecondaryOrderRequestConverter<ReleaseOrderRequest, ReleaseOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseRequestConverter.class);

    private final List<PaymentMethodRule> defaultRules;

    public ReleaseRequestConverter() {
        defaultRules = new ArrayList<>();

        defaultRules.add(new PaymentMethodRule.Builder()
                .setPaymentMethod(PaymentMethod.CREDIT_CARD)
                .setReleaseTime(0L)
                .setReleaseTimeUnit(ChronoUnit.DAYS)
                .setTakeRate(0L)
                .setTakeRateUnit(TakeRateUnit.PERCENT)
                .build());

        defaultRules.add(new PaymentMethodRule.Builder()
                .setPaymentMethod(PaymentMethod.CASH)
                .setReleaseTime(0L)
                .setReleaseTimeUnit(ChronoUnit.DAYS)
                .setTakeRate(0L)
                .setTakeRateUnit(TakeRateUnit.PERCENT)
                .build());

    }

    @Override
    public ReleaseOrder from(Order order, ReleaseOrderRequest orderRequest, RequestContext context) {
        Wallet wallet = context.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet da sessão."));

        ZonedDateTime now = ZonedDateTime.now();

        String orderUuid = UUID.randomUUID().toString();

        String secondaryId = context.getSecondaryId()
                .orElseThrow(() -> new AmeInvalidInputException("release_error", "Id do release é um campo obrigatório"));

        var rules = paymentMethodRulesConverter(order, orderRequest, orderRequest.getPaymentMethodRules());

        return new ReleaseOrder.Builder(order.getUuid())
                .setUuid(orderUuid)
                .setNsu(TransactionUtil.createNsu())
                .setStatus(OrderStatus.CREATED)
                .setCreatedByWalletId(wallet.getId().get())
                .setPaymentMethodRules(rules)
                .setTransactions(Collections.emptyList())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setSecondaryId(secondaryId)
                .build();
    }


    private List<PaymentMethodRule> paymentMethodRulesConverter(Order order, ReleaseOrderRequest orderRequest, List<PaymentMethodRuleRequest> paymentMethodRuleRequests) {

        if (paymentMethodRuleRequests != null && !paymentMethodRuleRequests.isEmpty()) {

            boolean isValid = validaeRules(order, orderRequest, paymentMethodRuleRequests);

            if(!isValid) {
                return defaultRules;
            }

            return paymentMethodRuleRequests
                    .stream()
                    .map(p ->
                            new PaymentMethodRule.Builder()
                                    .setPaymentMethod(p.getPaymentMethod())
                                    .setReleaseTime(p.getReleaseTime())
                                    .setReleaseTimeUnit(p.getReleaseTimeUnit())
                                    .setTakeRate(p.getTakeRate())
                                    .setTakeRateUnit(p.getTakeRateUnit())
                                    .build())
                    .collect(Collectors.toList());
        }

        return defaultRules;
    }

    /**
     * Valida de o payment method foi informada na regra pois é obrigatório.
     * @param order
     * @param orderRequest
     * @param paymentMethodRuleRequests
     */
    private boolean validaeRules(Order order, ReleaseOrderRequest orderRequest, List<PaymentMethodRuleRequest> paymentMethodRuleRequests) {

        if(paymentMethodRuleRequests != null && !paymentMethodRuleRequests.isEmpty() && paymentMethodRuleRequests.size() > 0) {
            boolean notValidPaymentMethods = paymentMethodRuleRequests
                    .stream()
                    .anyMatch(rule -> rule.getPaymentMethod() == null);

            if(notValidPaymentMethods) {
                LOG.error("Não foi informado o 'paymentMethod' para as regras de liquidacao da ordem [{}] releaseOrderRequest: [   {}   ]", order.getUuid(), orderRequest);
//                throw new AmeInvalidInputException("invalid_rules", "Não foi informado o 'paymentMethod' para as regras de liquidacao");
                return false;
            } else {
                return true;
            }
        }

        return false;
    }


}