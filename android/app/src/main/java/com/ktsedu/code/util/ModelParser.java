package com.ktsedu.code.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO:功能说明
 *
 * @author: damon
 * @date: 2015-09-17 13:13
 */
public class ModelParser {

    private static final String TAG = ModelParser.class.getSimpleName();
//    Gson gson = new Gson();
//    PayEntity payEntity = gson.fromJson(result, PayEntity.class);
    //NetBookModel bookModel = ModelParser.parseModel(result, NetBookModel.class);
    public static <T> T parseModel(String json, Class<T> classOfT) {
        T t = null;
        try {
            t = createGsonInstance().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, e.getLocalizedMessage());
            try {
                return classOfT.getDeclaredConstructor().newInstance();
            } catch (Exception e1) {
                Log.e(TAG, e1.getLocalizedMessage());
            }
        }
        return t;
    }

    public static <T> List<T> parseModeList(String json, Class<T[]> classOfT) {
        List<T> listT = null;
        try {
            T[] arr = createGsonInstance().fromJson(json, classOfT);
            return Arrays.asList(arr);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, e.getLocalizedMessage());
            listT = new ArrayList<T>();
        }
        return listT;
    }

    // obj to json
    public static String toModeJson(Object obj) {
        String json = "";
        try {
            json = createGsonInstance().toJson(obj);
            return json;
        } catch (JsonSyntaxException e) {
            Log.e(TAG, e.getLocalizedMessage());
            json = "";
        }
        return json;
    }
    public static <T> String toModeJson(Object obj, Class<T> classOfT) {
        String json = "";
        try {
            json = createGsonInstance().toJson(obj, classOfT);
            return json;
        } catch (JsonSyntaxException e) {
            Log.e(TAG, e.getLocalizedMessage());
            json = "";
        }
        return json;
    }

    private static Gson createGsonInstance() {
        return new Gson();
    }

}
