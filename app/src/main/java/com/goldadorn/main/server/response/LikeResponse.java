package com.goldadorn.main.server.response;

/**
 * Created by nithinjohn on 14/03/16.
 */
public class LikeResponse extends BasicResponse {

    public int userId = -1;
    public int collectionId = -1;
    public int productId = -1;
    public String data;

    public int currentLikeCountToWrite = 0;
}
