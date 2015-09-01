package com.xxx.transaction.tranfser.util;

import com.xxx.transaction.tranfser.rest.RootResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by ricdong on 15-8-30.
 */
public class OptionParser {

    /**
     * Convert the json string to an array of pair[user_name, coins]
     * @param jsonStr
     * @return
     * @throws JSONException
     */
    public static List<String[]> convertToPairCoin(String jsonStr)throws JSONException {
        ArrayList<String[]> arr = new ArrayList<String[]>();
        JSONObject parser = new JSONObject(jsonStr);

        JSONArray tuples = parser.getJSONObject("transaction").getJSONArray("useradd");

        for(int i = 0; i < tuples.length(); i++) {
            String[] tuple = new String[2];
            tuple[0] = tuples.getJSONObject(i).get("user_id").toString();
            tuple[1] = tuples.getJSONObject(i).get("coins").toString();
            arr.add(tuple);
        }

        return arr;
    }

    /**
     * Convert the plain text to Transaction object representation.
     * @param param Such as from_user_name=1&to_user_name=2&coins=500
     * @param paramObj
     * @throws IllegalArgumentException
     */
    public static void convertToTransaction(String param, RootResource.Transaction paramObj) {
        if(param == null) {
            throw new IllegalArgumentException("Param should not be null");
        }

        String[] pairs = param.split("&");
        if(pairs.length != 3) {
            throw new IllegalArgumentException("Invalid parameters for " + param);
        }

        HashMap<String, String> attr = new LinkedHashMap<String, String>();
        for(String pair : pairs) {
            String[] keyValue = pair.split("=");
            if(keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid parameters for " + param);
            }
            attr.put(keyValue[0], keyValue[1]);
        }

        paramObj.setFrom_user(checkNotNull(attr.get("from_user_name")));
        paramObj.setTo_user(checkNotNull(attr.get("to_user_name")));
        paramObj.setCoins(Integer.parseInt(checkNotNull(attr.get("coins"))));
    }
}
