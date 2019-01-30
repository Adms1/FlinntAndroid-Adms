package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by flinnt-android-1 on 20/2/17.
 */

public class BrowseCourseCategories implements Serializable {
    public static final String TYPE_KEY = "type";
    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";

    private String type = "";
    private String id = "";
    private String name = "";


    public synchronized void parseJSONObject(JSONObject jObject) {

        try {
            if (jObject.has(TYPE_KEY)) setType(jObject.getString(TYPE_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(ID_KEY)) setId(jObject.getString(ID_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }

        try {
            if (jObject.has(NAME_KEY)) setName(jObject.getString(NAME_KEY));
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
