//package com.amedigital.wallet.security;
//
//import com.amedigital.wallet.exceptions.AmeInvalidInputException;
//import com.b2wdigital.bpay.oauth2.token.Token;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//class TokenAuthenticationTest {
//
//    @Test
//    @DisplayName("Deve lançar exceção quando o payload do token for vázio.")
//    void test1() {
//        Set<String> scopes = new HashSet<>();
//        scopes.add("scopo1");
//
//        Token token = new Token.Builder()
//                .withClientId("1234")
//                .withPayload(new HashMap<>())
//                .withScopes(scopes)
//                .build();
//
//
//        assertThrows(AmeInvalidInputException.class, () -> new TokenAuthentication(new WalletToken(token)));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando o payload do token for nulo.")
//    void test2() {
//        Set<String> scopes = new HashSet<>();
//        scopes.add("scopo1");
//
//        Token token = new Token.Builder()
//                .withClientId("1234")
//                .withPayload(null)
//                .withScopes(scopes)
//                .build();
//
//        assertThrows(AmeInvalidInputException.class, () -> new TokenAuthentication(new WalletToken(token)));
//    }
//
//}
