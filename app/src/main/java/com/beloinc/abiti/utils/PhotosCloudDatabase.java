package com.beloinc.abiti.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotosCloudDatabase implements Serializable{

    private Map<String, String> photoUrls = new HashMap<>();
    private Map<String, String> description = new HashMap<>();
    private List<String> tags = new ArrayList<>();
    private String userOwner;
    private int countLeft;
    private int countRight;
    private String docId;
    private List<String> votersId;

    public PhotosCloudDatabase() {
    }

    public PhotosCloudDatabase(Map<String, String> photoUrls, Map<String, String> description, List<String> tags, String userOwner, int countLeft, int countRight, String docId, List<String> votersId) {
        this.photoUrls = photoUrls;
        this.description = description;
        this.tags = tags;
        this.userOwner = userOwner;
        this.countLeft = countLeft;
        this.countRight = countRight;
        this.docId = docId;
        this.votersId = votersId;
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

    public int getCountLeft() {
        return countLeft;
    }

    public int getCountRight() {
        return countRight;
    }

    public String getDocId() {
        return docId;
    }

    public List<String> getVotersId() {
        return votersId;
    }
}
