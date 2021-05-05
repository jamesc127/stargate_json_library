package com.datastax.astra;

import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AstraDB {
    private String DatabaseId;
    private String CloudRegion;
    private String Keyspace;
    private String Collection;
    private String ApplicationToken;

    public AstraDB(String DatabaseId,String CloudRegion,String Keyspace,String Collection,String ApplicationToken){
        this.DatabaseId = DatabaseId;
        this.CloudRegion = CloudRegion;
        this.Keyspace = Keyspace;
        this.Collection = Collection;
        this.ApplicationToken = ApplicationToken;
    }

    public String getDatabaseId(){
        return this.DatabaseId;
    }
    public void setDatabaseId(String dbId){
        this.DatabaseId = dbId;
    }

    public String getCloudRegion(){
        return this.CloudRegion;
    }
    public void setCloudRegion(String region){
        this.CloudRegion = region;
    }

    public String getKeyspace(){
        return this.Keyspace;
    }
    public void setKeyspace(String keyspace){
        this.Keyspace = keyspace;
    }

    public String getCollection(){
        return this.Collection;
    }
    public void setCollection(String collection){
        this.Collection = collection;
    }

    public String getApplicationToken(){
        return this.ApplicationToken;
    }
    public void setApplicationToken(String applicationToken){
        this.ApplicationToken = applicationToken;
    }










    private String fields(String field){
        return "fields="+field;
    }




}


