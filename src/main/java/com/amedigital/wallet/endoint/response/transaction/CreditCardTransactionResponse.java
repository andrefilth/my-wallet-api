package com.amedigital.wallet.endoint.response.transaction;

import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class CreditCardTransactionResponse extends TransactionResponse {

    private final String cardId;
    private final Integer numberOfInstallments;
    private final String cardMasked;
    private final String cardBrand;

    public CreditCardTransactionResponse(CreditCardTransaction creditCardTransaction) {
        super(creditCardTransaction);

        this.cardId = creditCardTransaction.getCreditCardId();
        this.numberOfInstallments = creditCardTransaction.getNumberOfInstallments();
        this.cardMasked = creditCardTransaction.getMaskedNumber();
        this.cardBrand = creditCardTransaction.getBrand();
    }

    public String getCardId() {
        return cardId;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public String getCardMasked() {
        return cardMasked;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("id", id)
                .append("type", type)
                .append("paymentMethod", paymentMethod)
                .append("amountInCents", amountInCents)
                .append("status", status)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("cardId", cardId)
                .append("numberOfInstallments", numberOfInstallments)
                .append("cardMasked", cardMasked)
                .append("cardBrand", cardBrand)
                .append("walletId", walletId)
                .append("peerWalletId", peerWalletId)
                .build();
    }
}
