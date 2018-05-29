package com.beloinc.abiti.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDatabase {

    private Map<String, String> photoUrls = new HashMap<>();
    private List<String> tags = new ArrayList<>();
    private Map<String, String> description = new HashMap<>();

    public UserDatabase() {
    }

    public UserDatabase(Map<String, String> photoUrls, List<String> tags, Map<String, String> description) {
        this.photoUrls = photoUrls;
        this.tags = tags;
        this.description = description;
    }

    public Map<String, String> getPhotoUrls() {
        return photoUrls;
    }

    public List<String> getTags() {
        return tags;
    }

    public Map<String, String> getDescription() {
        return description;
    }
}
