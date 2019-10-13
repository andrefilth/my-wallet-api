package com.amedigital.wallet.config;

import org.testcontainers.containers.GenericContainer;

public class DynamoDBContainer {

	private final GenericContainer<?> genericContainer;
	
    public DynamoDBContainer(final GenericContainer<?> genericContainer) {
        this.genericContainer = genericContainer;
    }

	public GenericContainer<?> getGenericContainer() {
		return genericContainer;
	}
}
