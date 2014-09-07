package Converter.ModelController.Controller.DB.NoSQL.Cauch;

import Converter.ModelController.CauchModel.CauchDataBase;
import Converter.ModelController.CauchModel.CauchEntitySchema;
import Converter.ModelController.CauchModel.CauchFieldSchema;
import Converter.ModelController.Controller.DB.NoSQL.NoSQLDataBaseOperations;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by szef on 2014-09-04.
 */
public class CauchDataBaseOperations extends NoSQLDataBaseOperations {

    private static final CauchDataBaseOperations ourInstance = new CauchDataBaseOperations();
    private CauchDataBase cauchDataBase;

    public static CauchDataBaseOperations getInstance() {
        return ourInstance;
    }

    @Override
    public List<String> GetDataBaseNames() {

        List<String> test = new ArrayList<String>();

        test.add("test");

        return test;
    }

    @Override
    public List<String> loadDataBase(String dbName) {

        cauchDataBase = new CauchDataBase();
        loadIntoMemory(dbName);



/*            View view = CauchConnector.getCauchClient(getNoSqlType()).getView("brewery_beers");
            Query query = new Query();
            query.setIncludeDocs(true).setLimit(5); // include all docs and limit to 5
            ViewResponse result = CauchConnector.getCauchClient(getNoSqlType()).query(view, query);*/


        return null;
    }

    @Override
    public List<List<String>> showFields(String dbName, String EntityName) throws UnknownHostException {
        return null;
    }

    @Override
    public void loadIntoMemory(String dbName) {

        List<JsonObject> list = null;
        try {
            list = CauchConnector.getCauchClient(null).view("types/types").query(JsonObject.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (JsonObject object : list) {

            getObjectsDataAndCreateSchema(object);
        }


    }

    private void getObjectsDataAndCreateSchema(JsonObject jsonObject) {

        CauchEntitySchema entitySchema = cauchDataBase.createOrGet(jsonObject.get("key").toString());
        CauchFieldSchema fields = new CauchFieldSchema();

        JsonObject values = jsonObject.get("value").getAsJsonObject();



        for (Map.Entry<String, JsonElement> currentField : values.entrySet()) {
            CauchFieldSchema fieldsaa = new CauchFieldSchema();
        }

        //entitySchema.appendFieldSchemas()
    }
}
