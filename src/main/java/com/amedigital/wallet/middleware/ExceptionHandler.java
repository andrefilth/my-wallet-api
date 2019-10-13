package com.amedigital.wallet.middleware;

import com.amedigital.wallet.endoint.response.serializer.ExceptionParserInterface;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.CodecException;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class ExceptionHandler implements WebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);
    private final ObjectMapper mapper;
    private final DataBufferFactory dataBufferFactory;
    private final AmeInternalException internalException;

    @Autowired
    public ExceptionHandler(ObjectMapper mapper, DataBufferFactory dataBufferFactory, AmeInternalException internalException) {
        this.mapper = mapper;
        this.dataBufferFactory = dataBufferFactory;
        this.internalException = internalException;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ExceptionParserInterface parser = (ame) -> {
            exchange.getResponse().setStatusCode(HttpStatus.resolve(ame.getHttpStatus()));
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            var body = Mono.just(dataBufferFactory.wrap(mapper.writeValueAsBytes(ame)));
            return exchange.getResponse().writeWith(body);
        };

        try {
            if (ex instanceof AmeException) {
                AmeException ex1 = (AmeException) ex;
                LOG.info("Ocorreu a seguinte exception: [{}]", ex1);

                return Mono.from(parser.parse(ex1));

            } else if (ex instanceof ServerWebInputException) {
                if (((ServerWebInputException) ex).getRootCause() instanceof AmeInvalidInputException) {
                    var cause = (AmeInvalidInputException) ((ServerWebInputException) ex).getRootCause();
                    LOG.info("Causa da exception [{}]", cause);

                    return Mono.from(parser.parse(cause));
                }

                return Mono.from(parser.parse(new AmeInvalidInputException((ServerWebInputException) ex)));

            } else if (ex instanceof CodecException) {
                AmeInvalidInputException rootCause = (AmeInvalidInputException) ((CodecException) ex).getRootCause();

                LOG.info("Ocorreu a seguinte exception: [{}]", rootCause);

                return Mono.from(parser.parse(rootCause));

            } else {
                LOG.info("Ocorreu a seguinte exception: [{}]", internalException);

                return Mono.from(parser.parse(internalException));
            }
        } catch (JsonProcessingException e) {
            LOG.error("Não foi possível mapear a exceção");
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        } finally {
            LOG.error("Ocorreu a seguinte exception: [{}]", ex);
        }
    }
}