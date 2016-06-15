package com.goldadorn.main.dj.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 26-05-2016.
 */
public class NotificationDataObject implements Parcelable {

    private String peopleImageUrl;
    private String notifyContent;
    private String dateTime;
    private String postImageUrl;
    private boolean botPost;
    private String postId;
    private String actionType;

    public NotificationDataObject(String peopleImageUrl, String notifyContent, String dateTime,
                                  String postImageUrl, boolean botPost, String postId, String actionType) {
        this.peopleImageUrl = peopleImageUrl;
        this.notifyContent = notifyContent;
        this.dateTime = dateTime;
        this.postImageUrl = postImageUrl;
        this.botPost = botPost;
        this.postId = postId;
        this.actionType = actionType;
    }

    public String getPostId() {
        return postId;
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

    public String getActionType() {
        return actionType;
    }

    protected NotificationDataObject(Parcel in) {
        peopleImageUrl = in.readString();
        notifyContent = in.readString();
        dateTime = in.readString();
        postImageUrl = in.readString();
        postId = in.readString();
        botPost = in.readByte() != 0x00;
        actionType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(peopleImageUrl);
        dest.writeString(notifyContent);
        dest.writeString(dateTime);
        dest.writeString(postImageUrl);
        dest.writeString(postId);
        dest.writeByte((byte) (botPost ? 0x01 : 0x00));
        dest.writeString(actionType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NotificationDataObject> CREATOR = new Parcelable.Creator<NotificationDataObject>() {
        @Override
        public NotificationDataObject createFromParcel(Parcel in) {
            return new NotificationDataObject(in);
        }

        @Override
        public NotificationDataObject[] newArray(int size) {
            return new NotificationDataObject[size];
        }
    };
}