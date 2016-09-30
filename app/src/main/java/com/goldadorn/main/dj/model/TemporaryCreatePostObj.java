package com.goldadorn.main.dj.model;

import java.io.File;
import java.util.Arrays;

/**
 * Created by User on 08-07-2016.
 */
public class TemporaryCreatePostObj {

    private int postType;
    private String msg;
    private File[] fileList;
    private String[] fileUriList;
    private String[] linksList;
    private String[] clubbedList;
    private String uploadedImages;
    private int postId;


    @Override
    public String toString() {
        return "TemporaryCreatePostObj{" +
                "postType=" + postType +
                ", msg='" + msg + '\'' +
                ", fileList=" + Arrays.toString(fileList) +
                ", fileUriList=" + Arrays.toString(fileUriList) +
                ", linksList=" + Arrays.toString(linksList) +
                ", clubbedList=" + Arrays.toString(clubbedList) +
                ", uploadedImages=" + uploadedImages +
                ", postId=" + postId +
                '}';
    }


    public String getUploadedImages() {
        return uploadedImages;
    }

    public void setUploadedImages(String uploadedImages) {
        this.uploadedImages = uploadedImages;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public File[] getFileList() {
        return fileList;
    }

    public void setFileList(File[] fileList) {
        this.fileList = fileList;
    }

    public String[] getFileUriList() {
        return fileUriList;
    }

    public void setFileUriList(String[] fileUriList) {
        this.fileUriList = fileUriList;
    }

    public String[] getLinksList() {
        return linksList;
    }

    public void setLinksList(String[] linksList) {
        this.linksList = linksList;
    }

    public String[] getClubbedList() {
        return clubbedList;
    }

    public void setClubbedList(String[] clubbedList) {
        this.clubbedList = clubbedList;
    }
}
