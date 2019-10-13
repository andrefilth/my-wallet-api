package com.amedigital.wallet.service;

import com.amedigital.wallet.security.WalletToken;
import reactor.core.publisher.Mono;

public interface AuthorizationService {

    Mono<WalletToken> authorize(String authorization);

}
