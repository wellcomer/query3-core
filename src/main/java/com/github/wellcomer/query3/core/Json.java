package com.github.wellcomer.query3.core;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * <h3>JSON конвертор.</h3>
 * Created on 05.11.15.
 */
public class Json {

    public static LinkedHashMap<String,String> toMap(String jsonQuery) throws ParseException, IOException {

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(jsonQuery);

        Set keys = json.keySet();
        Iterator iter = keys.iterator();

        String k,v;

        LinkedHashMap<String,String> queryMap = new LinkedHashMap<>();

        while (iter.hasNext()) {
            k = (String) iter.next();
            v = (String) json.get(k);
            queryMap.put(k.trim(), JSONObject.escape(v).trim());
        }
        return queryMap;
    }

    public static String toString(Object queryMap){
        return JSONValue.toJSONString(queryMap);
    }
}
