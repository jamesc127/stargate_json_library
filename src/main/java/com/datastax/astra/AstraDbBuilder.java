package com.datastax.astra;

public class AstraDbBuilder {
    private String databaseId;
    private String cloudRegion;
    private String keyspace;
    private String collection;
    private String applicationToken;

    public AstraDbBuilder setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
        return this;
    }

    public AstraDbBuilder setCloudRegion(String cloudRegion) {
        this.cloudRegion = cloudRegion;
        return this;
    }

    public AstraDbBuilder setKeyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }

    public AstraDbBuilder setCollection(String collection) {
        this.collection = collection;
        return this;
    }

    public AstraDbBuilder setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
        return this;
    }

    public AstraDB createAstraDB() {
        return new AstraDB(databaseId, cloudRegion, keyspace, collection, applicationToken);
    }
}