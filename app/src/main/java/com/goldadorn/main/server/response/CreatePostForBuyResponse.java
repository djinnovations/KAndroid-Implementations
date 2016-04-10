package com.goldadorn.main.server.response;

/**
 * Created by nithinjohn on 10/04/16.
 */
public class CreatePostForBuyResponse extends BasicResponse {
    public  final int type;
    public final String message;
    public final String imageurl;

    public CreatePostForBuyResponse(int type, String message, String imageurl) {
        this.type = type;
        this.message = message;
        this.imageurl = imageurl;
    }
}
