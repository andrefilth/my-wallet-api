//package com.amedigital.wallet.middleware;
//
//import com.amedigital.wallet.exceptions.AmeException;
//import com.amedigital.wallet.exceptions.AmeInternalException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.core.io.buffer.DefaultDataBufferFactory;
//import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
//import org.springframework.mock.web.server.MockServerWebExchange;
//
//import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
//import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
//import static org.assertj.core.api.Assertions.assertThat;
//
//class ExceptionHandlerTest {
//
//    @Test
//    @DisplayName("Deve filtrar um request com uma exceção externa")
//    void shouldConsumeHealthEndpoint() {
//        MockServerHttpRequest.BaseBuilder<?> baseBuilder = MockServerHttpRequest.get("/");
//
//        MockServerWebExchange web = MockServerWebExchange.from(baseBuilder);
//
//        ExceptionHandler w = new ExceptionHandler(new ObjectMapper(), new DefaultDataBufferFactory(), new AmeInternalException());
//
//        w.handle(web, new RuntimeException("")).block();
//
//        assertThat(web.getResponse().getStatusCodeValue()).isEqualTo(SC_INTERNAL_SERVER_ERROR);
//
//    }
//
//    @Test
//    @DisplayName("Deve consultar o endpoint /health com AmeException")
//    void shouldConsumeHealthEndpoinst() {
//
//        MockServerHttpRequest.BaseBuilder<?> baseBuilder = MockServerHttpRequest.get("/");
//
//        MockServerWebExchange web = MockServerWebExchange.from(baseBuilder);
//
//        ExceptionHandler w = new ExceptionHandler(new ObjectMapper(), new DefaultDataBufferFactory(), new AmeInternalException());
//
//        w.handle(web, new AmeException(SC_BAD_REQUEST, "error", "error")).block();
//
//        assertThat(web.getResponse().getStatusCodeValue()).isEqualTo(SC_BAD_REQUEST);
//
//    }
//
//}
