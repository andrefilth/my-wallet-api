package com.amedigital.wallet.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.util.json.Jackson;
import com.amedigital.wallet.model.dynamodb.WalletOperationAttribute;
import com.amedigital.wallet.repository.DynamoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DynamoRepositoryImpl implements DynamoRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoRepositoryImpl.class);

    private final DynamoDBMapper mapper;
    private final ObjectMapper objectMapper;
    private final DynamoDB dynamoDB;
    private final Table table;


    @Autowired
    public DynamoRepositoryImpl(ObjectMapper oMapper, DynamoDB dynamoDB, DynamoDBMapper mapper) {
        this.mapper = mapper;
        this.objectMapper = oMapper;
        this.dynamoDB = dynamoDB;

        String tableName = mapper.generateCreateTableRequest(WalletOperationAttribute.class).getTableName();
        this.table = dynamoDB.getTable(tableName);
    }

    @Override
    public Mono<Map<String, Object>> findById(final String orderDetailUuid) {
        if (orderDetailUuid != null && !orderDetailUuid.isEmpty()) {
            try {
                Item item = table.getItem("id", orderDetailUuid);

                if (item == null) {
                    Mono.empty();
                }

                Map<String, Object> result = new HashMap<>();

                if (item == null)
                    return Mono.empty();

                if (item.isPresent("attributes")) {
                    Jackson.fromJsonString((String) item.get("attributes"), Map.class)
                            .forEach((k, v) -> item.with((String) k, v));

                    item.removeAttribute("attributes");

                    table.putItem(item);
                }

                item.removeAttribute("id");

                item.attributes().forEach(e -> result.put(e.getKey(), e.getValue()));

                if (result.containsKey("itens")) {
                    Object itens = result.get("itens");
                    result.remove("itens");
                    result.put("items", itens);
                }

                return Mono.justOrEmpty(result);
            } catch (Exception e) {
                LOG.error("Aconteceu erro para pegar o customPayload no DynamoDB", e);
            }
        }

        return Mono.empty();
    }

    @Override
    public Mono<Map<String, Object>> save(String orderDetailUuid, Map<String, Object> walletOperationAttribute) {
        if (walletOperationAttribute != null && !walletOperationAttribute.isEmpty()) {

            if (orderDetailUuid != null && !orderDetailUuid.isEmpty()) {

                LOG.info("Salvando/Atualizando customPayload [{}]", walletOperationAttribute);

                table.deleteItem("id", orderDetailUuid);

                Item item = new Item().withPrimaryKey("id", orderDetailUuid);


                walletOperationAttribute.forEach(item::with);

                table.putItem(item);

                return Mono.justOrEmpty(walletOperationAttribute);
            }
        }

        return Mono.just(Collections.emptyMap());
    }
}
