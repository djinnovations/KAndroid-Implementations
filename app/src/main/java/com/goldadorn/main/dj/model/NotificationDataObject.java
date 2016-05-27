package com.goldadorn.main.dj.model;

/**
 * Created by User on 26-05-2016.
 */
public class NotificationDataObject {

    private String peopleImageUrl;
    private String notifyContent;
    private String dateTime;
    private String postImageUrl;
    private boolean botPost;

    public NotificationDataObject(String peopleImageUrl, String notifyContent, String dateTime,
                                  String postImageUrl, boolean botPost) {
        this.peopleImageUrl = peopleImageUrl;
        this.notifyContent = notifyContent;
        this.dateTime = dateTime;
        this.postImageUrl = postImageUrl;
        this.botPost = botPost;
    }

    public String getPeopleImageUrl() {
        return peopleImageUrl;
    }

    public String getNotifyContent() {
        return notifyContent;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public boolean isBotPost() {
        return botPost;
    }
}
