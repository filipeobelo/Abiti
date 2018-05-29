package com.beloinc.abiti.utils;


public class PhotosDatabase {

    private String photoLeftUrl;
    private String photoRightUrl;
    private String[] tags;
    private String leftDescription;
    private String rightDescription;

    public PhotosDatabase() {
    }

    public PhotosDatabase(String photoLeftUrl, String photoRightUrl, String[] tags, String leftDescription, String rightDescription) {
        this.photoLeftUrl = photoLeftUrl;
        this.photoRightUrl = photoRightUrl;
        this.tags = tags;
        this.leftDescription = leftDescription;
        this.rightDescription = rightDescription;
    }

    public String getPhotoLeftUrl() {
        return photoLeftUrl;
    }

    public String getPhotoRightUrl() {
        return photoRightUrl;
    }

    public String[] getTags() {
        return tags;
    }

    public String getLeftDescription() {
        return leftDescription;
    }

    public String getRightDescription() {
        return rightDescription;
    }
}
