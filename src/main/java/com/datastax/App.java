package com.datastax;
import java.io.*;
import java.util.List;
import okhttp3.*;
import com.datastax.astra.AstraDB;
import com.datastax.astra.AstraDbBuilder;
import com.datastax.astra.AstraDocumentQuery;
import static com.datastax.astra.AstraDocumentQuery.Filters.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class App {
    public App() throws IOException, ParseException {
    }
    public static void main(String[] args) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        AstraDB db = new AstraDbBuilder()
                            .setDatabaseId("")
                            .setCloudRegion("")
                            .setKeyspace("")
                            .setCollection("")
                            .setApplicationToken("")
                            .createAstraDB();

        long StartTime = System.currentTimeMillis();

        AstraDocumentQuery query = new AstraDocumentQuery(db);

        //Read JSON data from file and create an array
        //Load the document into Astra
        JSONArray json = (JSONArray) jsonParser.parse(new FileReader("./src/main/resources/MOCK_DATA.json"));
        List<Response> insertResult = query.insert(json);

        //Load car data too
        JSONArray carJson = (JSONArray) jsonParser.parse(new FileReader("./src/main/resources/MOCK_DATA_CAR.json"));
        List<Response> insertCarResult = query.insert(carJson);

        long EndTime = System.currentTimeMillis();
        long TotalTime = (EndTime - StartTime)/1000;
        System.out.println("Total time to load 200 JSON documents was "+TotalTime+" seconds.");

        JSONArray result = query.find(eq("favorite.color","Blue")).all();
        System.out.println(result.toString());

        JSONArray carYearGt = query.find(gte("car.year",2005)).all();
        System.out.println(carYearGt.toString());
    }
}
