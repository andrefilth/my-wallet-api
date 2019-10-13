package com.amedigital.wallet.service.impl;

import com.amedigital.wallet.constants.enuns.DocumentType;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.OwnerService;
import com.amedigital.wallet.service.WalletService;
import com.b2wdigital.bpay.util.validation.CNPJValidator;
import com.b2wdigital.bpay.util.validation.CPFValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

import static com.amedigital.wallet.constants.enuns.DocumentType.CPF;
import static com.amedigital.wallet.constants.enuns.WalletType.CUSTOMER;
import static com.amedigital.wallet.constants.enuns.WalletType.MERCHANT;
import static com.amedigital.wallet.util.ValidatorUtil.notEmpty;
import static com.amedigital.wallet.util.ValidatorUtil.notNull;
import static java.util.UUID.randomUUID;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger LOG = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository repository;
    private final OwnerService ownerService;

    @Autowired
    public WalletServiceImpl(WalletRepository repository, OwnerService ownerService) {
        this.repository = repository;
        this.ownerService = ownerService;
    }

    @Override
    public Mono<Wallet> create(Wallet wallet) {
        ZonedDateTime now = ZonedDateTime.now();

        var ownerWithUUID = wallet.getOwner()
                .copy()
                .setUuid(randomUUID().toString())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();

        var walletToPersist = wallet.copy()
                .setOwner(ownerWithUUID)
                .setUuid(randomUUID().toString())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();

        return validateFields(wallet)
                .then(validateDocument(walletToPersist))
                .thenMany(validateWallet(walletToPersist))
                .then(repository.insert(walletToPersist));
    }

    @Override
    public Mono<Wallet> update(Wallet wallet, final String walletId) {
        LOG.info("Realizando a alteração da carteira [{}]", walletId);

        return validateFields(wallet)
                .then(validateDocument(wallet))
                .then(findByUuid(walletId))
                .flatMap(w -> validateForUpdate(wallet, w))
                .flatMap(repository::update);
    }

    @Override
    public Mono<Wallet> findByUuid(final String walletId) {
        return repository.findByUuid(walletId);
    }

    @Override
    public Mono<Wallet> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Wallet> findByOwnerUuid(final String ownerUuid) {
        return repository.findByOwnerUuid(ownerUuid);
    }

    private Mono<Void> validateFields(Wallet wallet) {
        notEmpty(wallet.getName(), "nome da carteira");
        notNull(wallet.getType(), "tipo da carteira");

        notNull(wallet.getOwner(), "proprietário");
        notEmpty(wallet.getOwner().getName(), "nome do proprietário");
        notEmpty(wallet.getOwner().getExternalId(), "transactionId externo");
        notEmpty(wallet.getOwner().getEmail(), "email do proprietário");
        notEmpty(wallet.getOwner().getDocument(), "documento do proprietário");
        notNull(wallet.getOwner().getDocumentType(), "tipo do documento");

        LOG.info("Campos da wallet validados com sucesso. body: [{}]", wallet);
        return Mono.empty();
    }

    private Flux<Object> validateWallet(Wallet wallet) {
        var owner = wallet.getOwner();

        var ownerDocumentExists = checkIfDocumentExists(owner);

        var ownerEmailExists = checkIfEmailExists(owner);

        return Flux.merge(ownerEmailExists, ownerDocumentExists);
    }

    private Mono<Wallet> validateDocument(Wallet wallet) {
        var document = wallet.getOwner().getDocument();
        var walletType = wallet.getType();
        var documentType = wallet.getOwner().getDocumentType();

        if (walletType.equals(CUSTOMER) && documentType.equals(DocumentType.CNPJ)) {
            LOG.error("Tipo de documento inválido para uma wallet de customer body: [{}]",wallet);
            return Mono.error(new AmeInvalidInputException("document_wallet_incompatibility_error",
                    "Formato inválido, um CUSTOMER não pode ter um CNPJ."));
        }

        if (walletType.equals(MERCHANT) && documentType.equals(CPF)) {
            LOG.error("Tipo de documento inválido para uma wallet de merchant body: [{}]",wallet);
            return Mono.error(new AmeInvalidInputException("document_wallet_incompatibility_error",
                    "Formato inválido, um MERCHANT não pode ter um CPF"));
        }

        if (walletType.equals(CUSTOMER)) {
            if (!(new CPFValidator().isValid(document))) {
                LOG.error("CPF inváido body: [{}]",wallet);
                return Mono.error(new AmeInvalidInputException("cpf_parse_error", "Formato de CPF inválido."));
            }
        }

        if (walletType.equals(MERCHANT)) {
            if (!(new CNPJValidator().isValid(document))) {
                LOG.error("CNPJ: [{}]",wallet);
                return Mono.error(new AmeInvalidInputException("cnpj_parse_error", "Formato de CNPJ inválido."));
            }
        }

        return Mono.empty();
    }

    private Mono<Wallet> checkIfDocumentExists(Owner owner) {
        return ownerService.findByDocument(owner.getDocument())
                .flatMap($ -> {
                    LOG.error("Document do owner previamente cadastrado body: [{}]", owner);
                    return Mono.error(new AmeException(SC_BAD_REQUEST, "owner_document_duplication_error", "O documento do owner já existe."));
                });

    }

    private Mono<Wallet> checkIfEmailExists(Owner owner) {
        return ownerService.findByEmail(owner.getEmail())
                .flatMap($ -> {
                    LOG.error("Email do owner previamente cadastrado body: [{}]", owner);
                    return Mono.error(new AmeException(SC_BAD_REQUEST, "owner_email_duplication_error", "O email do owner já existe."));
                });
    }

    private Mono<? extends Wallet> validateForUpdate(Wallet wallet, Wallet w) {
        var isDocumentChanged = !w.getOwner().getDocument().equals(wallet.getOwner().getDocument());
        var isEmailChanged = !w.getOwner().getEmail().equals(wallet.getOwner().getEmail());

        ZonedDateTime now = ZonedDateTime.now();

        var updatedOwner = wallet.getOwner().copy()
                .setUuid(w.getOwner().getUuid().get())
                .setUpdatedAt(now)
                .build();

        var updatedWallet = wallet.copy()
                .setUuid(w.getUuid().get())
                .setOwner(updatedOwner)
                .setUpdatedAt(now)
                .build();

        if (isDocumentChanged && isEmailChanged) {
            return validateWallet(updatedWallet).then(Mono.just(updatedWallet));

        } else if (isDocumentChanged) {
            return checkIfDocumentExists(wallet.getOwner()).then(Mono.just(updatedWallet));

        } else if (isEmailChanged) {
            return checkIfEmailExists(wallet.getOwner()).then(Mono.just(updatedWallet));

        } else {
            return Mono.just(updatedWallet);
        }
    }

}
