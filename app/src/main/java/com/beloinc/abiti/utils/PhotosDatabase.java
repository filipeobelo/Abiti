package com.beloinc.abiti.utils;

public class PhotosDatabase {

    private String photoLeftUrl;
    private String photoRightUrl;
    private String tag;
    private String leftDescription;
    private String rightDescription;

    public PhotosDatabase() {
    }

    public PhotosDatabase(String photoLeftUrl, String photoRightUrl, String tag, String leftDescription, String rightDescription) {
        this.photoLeftUrl = photoLeftUrl;
        this.photoRightUrl = photoRightUrl;
        this.tag = tag;
        this.leftDescription = leftDescription;
        this.rightDescription = rightDescription;
    }

    public String getLeftDescription() {
        return leftDescription;
    }

    public void setLeftDescription(String leftDescription) {
        this.leftDescription = leftDescription;
    }

    public String getRightDescription() {
        return rightDescription;
    }

    public void setRightDescription(String rightDescription) {
        this.rightDescription = rightDescription;
    }

    public String getPhotoLeftUrl() {
        return photoLeftUrl;
    }

    public String getPhotoRightUrl() {
        return photoRightUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setPhotoLeftUrl(String photoLeftUrl) {
        this.photoLeftUrl = photoLeftUrl;
    }

    public void setPhotoRightUrl(String photoRightUrl) {
        this.photoRightUrl = photoRightUrl;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
