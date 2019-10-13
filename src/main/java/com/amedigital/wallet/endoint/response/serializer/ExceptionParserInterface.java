package com.amedigital.wallet.endoint.response.serializer;

import com.amedigital.wallet.exceptions.AmeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.reactivestreams.Publisher;

public interface ExceptionParserInterface {

    Publisher<Void> parse(AmeException ex) throws JsonProcessingException;

}
