package com.amedigital.wallet.service.impl;

import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.security.WalletToken;
import com.amedigital.wallet.service.AuthorizationService;
import com.b2wdigital.bpay.oauth2.exception.OAuth2Exception;
import com.b2wdigital.bpay.oauth2.service.AuthClient;
import com.b2wdigital.bpay.util.exception.BPayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

    private final AuthClient authClient;

    @Autowired
    public AuthorizationServiceImpl(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Mono<WalletToken> authorize(String authorization) {

        return Mono.fromCallable(() -> new WalletToken(authClient.authenticate(authorization)))
                .onErrorMap((ex) -> {
                    if (ex instanceof OAuth2Exception) {
                        var exception = (OAuth2Exception) ex;
                        var httpStatus = exception.getHttpStatus();
                        var errorCode = exception.getErrorCode();
                        var errorDescription = exception.getErrorDescription();

                        LOG.error("Nova exception, status: [{}], error_code: [{}], description: [{}], token: [[]]",
                                httpStatus, errorCode, errorDescription, authorization);

                        return new AmeException(httpStatus, errorCode, errorDescription);
                    } else if (ex instanceof BPayException) {
                        int httpStatus = ((BPayException) ex).getHttpStatus();
                        String errorCode = ((BPayException) ex).getErrorCode();
                        String errorDescription = ((BPayException) ex).getErrorDescription();

                        LOG.error("Nova exception, status: [{}], error_code: [{}], description: [{}], token: [[]]",
                                httpStatus, errorCode, errorDescription, authorization);

                        return new AmeException(httpStatus, errorCode, errorDescription);
                    } else {
                        return new AmeInternalException();
                    }
                });
    }

}
