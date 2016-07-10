package com.goldadorn.main.dj.utils;

import android.util.Log;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.model.TemporaryCreatePostObj;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.google.gson.Gson;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;

import java.util.List;

/**
 * Created by User on 08-07-2016.
 */
public class TemporarySocialPostParser {

    protected static BaseDataParser loadedDataVO;

    public static void parseData(String url, Object value, Object status) {

        try {
            String e = (String) value;
            Class clazz = SocialFeedFragment.SocilFeedResult.class;
            Gson gson = new Gson();
            loadedDataVO = (BaseDataParser) gson.fromJson(e, clazz);
            loadedDataVO.typeCastData();

            List list = null;
            if (loadedDataVO.parseRequired()) {
                Class var12 = loadedDataVO.getManagedObjectClass();
                list = getResultList(loadedDataVO, var12);
            } else {
                list = loadedDataVO.getList();
                /*if(list != null) {
                    for(int i = 0; i < list.size(); ++i) {
                        if(list.get(i) instanceof IParseableObject) {
                            ((IParseableObject)list.get(i)).dataLoaded(loadedDataVO);
                        }
                    }
                }*/

            }

        } catch (Throwable var11) {
            Log.e("JSONDataManager", "JSON PARSING FAIL " + url);
            System.out.println(var11);
        }

    }


    private static List<?> getResultList(BaseDataParser loadedDataVO, Class<Object> iBaseObjectClass) {
        List list = loadedDataVO.getList();
        return list != null && list.size() != 0 ? loadedDataVO.typeCastList(list, iBaseObjectClass) : null;
    }


    public static SocialPost getSocialPostObj(TemporaryCreatePostObj tempPostObj) {
        Log.d("djpost", "tempPostObj- getSocialPostObj:  " + tempPostObj.toString());
        User user = Application.getInstance().getUser();
        ClubbedDataObj clubbedDataObj1 = null;
        ClubbedDataObj clubbedDataObj2 = null;
        ClubbedDataObj clubbedDataObj3 = null;
        for (int i = 0; i < tempPostObj.getClubbedList().length; i++) {
            try {
                if (i == 0)
                    clubbedDataObj1 = extractClubbedData(tempPostObj.getClubbedList()[i]);
                if (i == 1)
                    clubbedDataObj2 = extractClubbedData(tempPostObj.getClubbedList()[i]);
                if (i == 2)
                    clubbedDataObj3 = extractClubbedData(tempPostObj.getClubbedList()[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SocialPost socialPost = new SocialPost();
        socialPost.setUserId(user.id);
        socialPost.setPostId(String.valueOf(tempPostObj.getPostId()));
        socialPost.setUserName(user.getName());
        socialPost.setUserPic(user.getImageUrl());
        socialPost.setIsFollowing(1);
        socialPost.setDescription(tempPostObj.getMsg());
        socialPost.setTimestamp(System.currentTimeMillis());
        socialPost.setType(String.valueOf(tempPostObj.getPostType()));
        socialPost.setLikeCount(0);
        socialPost.setCommentCount(0);
        socialPost.setIsLiked(0);
        socialPost.setVoteCount(0);
        socialPost.setIsVoted(0);
        if (clubbedDataObj1 != null)
            socialPost.setPrice1(clubbedDataObj1.price);
        if (tempPostObj.getLinksList() != null)
            socialPost.setImage1(tempPostObj.getLinksList()[0]);

        if (tempPostObj.getPostType() == SocialPost.POST_TYPE_BEST_OF) {
            socialPost.setBof3Percent1(0);
            socialPost.setBof3Percent2(0);
            socialPost.setBof3Percent3(0);
            if (clubbedDataObj2 != null)
                socialPost.setPrice2(clubbedDataObj2.price);
            if (clubbedDataObj3 != null)
                socialPost.setPrice3(clubbedDataObj3.price);
            if (tempPostObj.getLinksList().length > 1)
                socialPost.setImage2(tempPostObj.getLinksList()[1]);
            if (tempPostObj.getLinksList().length > 2)
                socialPost.setImage3(tempPostObj.getLinksList()[2]);
        } else if (tempPostObj.getPostType() == SocialPost.POST_TYPE_POLL) {
            socialPost.setYesPercent(0);
            socialPost.setNoPercent(0);
        }
        socialPost.dataLoaded(null);
        return socialPost;
    }


    public static ClubbedDataObj extractClubbedData(String clubbedData) {
        String[] dataArr = clubbedData.split(":");
        ClubbedDataObj dataObj = new ClubbedDataObj();
        dataObj.prodId = dataArr[0];
        dataObj.collId = dataArr[1];
        dataObj.desId = dataArr[2];
        dataObj.price = dataArr[3];
        return dataObj;
    }


    public static class ClubbedDataObj {
        String prodId;
        String collId;
        String desId;
        String price;
    }
}
