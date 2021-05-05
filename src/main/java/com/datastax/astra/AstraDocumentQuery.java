//TODO Abstract this from the http client
//TODO Separate urlBuilders for insert and select
//TODO Page-state paging through responses
package com.datastax.astra;

import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AstraDocumentQuery {
    private AstraDB db;
    private OkHttpClient client;
    private MediaType mediaType;
    private JSONParser jsonParser;
    private String QUERYFILTER;

    private HttpUrl.Builder getHttpUrlBuilder(){
        assert db != null;
        return HttpUrl.get("https://"+db.getDatabaseId()+"-"+db.getCloudRegion()
                +".apps.astra.datastax.com/api/rest/v2/namespaces/"
                +db.getKeyspace()
                +"/collections/"+ this.db.getCollection())
                .newBuilder();
    }
    public AstraDocumentQuery(AstraDB db) {
        this.db = db;
        this.client = new OkHttpClient().newBuilder().readTimeout(30, TimeUnit.SECONDS).build();
        this.mediaType = MediaType.parse("application/json");
        this.jsonParser = new JSONParser();
    }

    private Request insertRequest(RequestBody body){
        Request request = new Request.Builder()
                .url(getHttpUrlBuilder().build())
                .method("POST", body)
                .addHeader("X-Cassandra-Token", this.db.getApplicationToken())
                .addHeader("Content-Type", "application/json")
                .build();
        return request;
    }
    public Response insert(JSONObject json){
        Response response = null;
        RequestBody jsonBody = RequestBody.create(json.toJSONString(),mediaType);
        Request request = insertRequest(jsonBody);
        try {
            response = client.newCall(request).execute();
            System.out.println(response.body().string()); //for demo purposes only
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public List<Response> insert(JSONArray jsonArray){
        List<Response> lResponse = new ArrayList<>();
        for (Object j:jsonArray) {
            RequestBody jsonBody = RequestBody.create(j.toString(),mediaType);
            Request request = insertRequest(jsonBody);
            try {
                Response r = client.newCall(request).execute();
                System.out.println(r.body().string()); //for demo purposes only
                lResponse.add(r);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lResponse;
    }

    private JSONArray findResponse(HttpUrl.Builder url) throws ParseException {
        String rBody = "";
        Request request = new Request.Builder().url(url.build())
                .method("GET", null)
                .addHeader("X-Cassandra-Token", this.db.getApplicationToken())
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(request.toString());
            rBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray objArray = new JSONArray();
        JSONObject obj = (JSONObject) jsonParser.parse(rBody);
        while (obj.containsKey("pageState")){
            objArray.add(obj);
            String pageState = obj.get("pageState").toString();
            obj = requestAnotherPage(pageState);
        }
        return objArray;
    }
    private JSONObject requestAnotherPage(String pageState) throws ParseException {
        String rBody = "";
        Request request = new Request.Builder()
                .url(getHttpUrlBuilder()
                    .addQueryParameter("where",this.QUERYFILTER)
                    .addQueryParameter("page-state",pageState)
                    .build()
                )
                .method("GET", null)
                .addHeader("X-Cassandra-Token", this.db.getApplicationToken())
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            rBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (JSONObject) jsonParser.parse(rBody);
    }
    public AstraDocumentQuery find(String filter) {
        this.QUERYFILTER = filter;
        return this;
    }

    public JSONArray first() throws ParseException {
        return findResponse(getHttpUrlBuilder()
                .addQueryParameter("where",this.QUERYFILTER)
                .addQueryParameter("page-size",String.valueOf(1)));
    }
    public JSONArray all() throws ParseException {
        return findResponse(getHttpUrlBuilder()
                            .addQueryParameter("where",this.QUERYFILTER)
                            .addQueryParameter("page-size",String.valueOf(1)));
    }

    public static class Filters{
        public static String gt(String key, Object value){
            return "{\"" + key + "\":{\"$gt\":\"" + value + "\"}}";
        }
        public static String gte(String key, Object value){
            return "{\"" + key + "\":{\"$gte\":\"" + value + "\"}}";
        }
        public static String lt(String key, Object value){
            return "{\"" + key + "\":{\"$lt\":\"" + value + "\"}}";
        }
        public static String lte(String key, Object value){
            return "{\"" + key + "\":{\"$lte\":\"" + value + "\"}}";
        }
        public static String eq(String key, Object value){
            return "{\"" + key + "\":{\"$eq\":\"" + value + "\"}}";
        }
        public static String neq(String key, Object value){
            return "{\"" + key + "\":{\"$neq\":\"" + value + "\"}}";
        }
    }

}
