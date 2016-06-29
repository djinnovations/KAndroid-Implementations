package com.goldadorn.main.dj.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26-05-2016.
 */
public class NotificationDataObject implements Parcelable {

    private String peopleImageUrl;
    private String notifyContent;
    private String dateTime;
    private List<String> postImageUrl;
    private boolean botPost;
    private String postId;
    private String actionType;

    public NotificationDataObject(String peopleImageUrl, String notifyContent, String dateTime,
                                  List<String> postImageUrl, boolean botPost, String postId, String actionType) {
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

    public List<String> getPostImageUrl() {
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
        if (in.readByte() == 0x01) {
            postImageUrl = new ArrayList<String>();
            in.readList(postImageUrl, String.class.getClassLoader());
        } else {
            postImageUrl = null;
        }
        botPost = in.readByte() != 0x00;
        postId = in.readString();
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
        if (postImageUrl == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(postImageUrl);
        }
        dest.writeByte((byte) (botPost ? 0x01 : 0x00));
        dest.writeString(postId);
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