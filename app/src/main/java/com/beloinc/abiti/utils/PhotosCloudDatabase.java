package com.beloinc.abiti.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotosCloudDatabase {

    private Map<String, String> photoUrls = new HashMap<>();
    private Map<String, String> description = new HashMap<>();
    private List<String> tags = new ArrayList<>();
    private String userOwner;

    public PhotosCloudDatabase() {
    }

    public PhotosCloudDatabase(Map<String, String> photoUrls, Map<String, String> description, List<String> tags, String userOwner) {
        this.photoUrls = photoUrls;
        this.description = description;
        this.tags = tags;
        this.userOwner = userOwner;
    }

    public Map<String, String> getPhotoUrls() {
        return photoUrls;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getUserOwner() {
        return userOwner;
    }
}
