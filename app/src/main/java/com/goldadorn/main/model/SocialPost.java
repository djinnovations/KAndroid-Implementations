package com.goldadorn.main.model;

import android.text.TextUtils;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.model.RecommendedProduct;
import com.goldadorn.main.dj.utils.SmartTimeAgo;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bhavinpadhiyar on 2/19/16.
 */
public class SocialPost extends ServerError implements IParseableObject {

    public static final Integer POST_TYPE_NORMAL_POST = 1;
    public static final Integer POST_TYPE_POLL = 2;
    public static final Integer POST_TYPE_BEST_OF = 3;
    public static final Integer POST_RECOMMENDED_PRODS = 4;
    public static final Integer POST_ATC = 8;
    public static final Integer POST_RATC = 11;
    public static final Integer POST_MOST_LIKED = 12;
    public static final Integer POST_TREDING_TAGS = 13;
    public static final Integer POST_TYPE_RECO_NEW = 15;
    public static final Integer POST_TYPE_REDEMPTION = 16;

    private int gold;
    private int diamond;
    private String pHdr1;
    private String pHdr2;
    private String pReward1;
    private String pReward2;
    private String pText1;
    private String pText2;
    private String pNote;

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public String getpHdr1() {
        return pHdr1;
    }

    public void setpHdr1(String pHdr1) {
        this.pHdr1 = pHdr1;
    }

    public String getpHdr2() {
        return pHdr2;
    }

    public void setpHdr2(String pHdr2) {
        this.pHdr2 = pHdr2;
    }

    public String getpReward1() {
        return pReward1;
    }

    public void setpReward1(String pReward1) {
        this.pReward1 = pReward1;
    }

    public String getpReward2() {
        return pReward2;
    }

    public void setpReward2(String pReward2) {
        this.pReward2 = pReward2;
    }

    public String getpText1() {
        return pText1;
    }

    public void setpText1(String pText1) {
        this.pText1 = pText1;
    }

    public String getpText2() {
        return pText2;
    }

    public void setpText2(String pText2) {
        this.pText2 = pText2;
    }

    public String getpNote() {
        return pNote;
    }

    public void setpNote(String pNote) {
        this.pNote = pNote;
    }

    private Integer isDesigner = 1;
    private Integer bof3Percent1;
    private Integer bof3Percent2;
    private Integer bof3Percent3;
    private List<Image> recommendation;
    private String recommendation1;
    private String recommendation2;
    private String recommendation3;
    private String recommendation4;

    private String range1;
    private String range2;
    private String range3;
    private String discount1;
    private String discount2;
    private String discount3;
    private String image1loc;
    private String image2loc;
    private String image3loc;

    private String tags;


    public String getRange1() {
        return range1;
    }

    public void setRange1(String range1) {
        this.range1 = range1;
    }

    public String getRange2() {
        return range2;
    }

    public void setRange2(String range2) {
        this.range2 = range2;
    }

    public String getRange3() {
        return range3;
    }

    public void setRange3(String range3) {
        this.range3 = range3;
    }

    public String getDiscount1() {
        return discount1;
    }

    public void setDiscount1(String discount1) {
        this.discount1 = discount1;
    }

    public String getDiscount2() {
        return discount2;
    }

    public void setDiscount2(String discount2) {
        this.discount2 = discount2;
    }

    public String getDiscount3() {
        return discount3;
    }

    public void setDiscount3(String discount3) {
        this.discount3 = discount3;
    }

    //private List<>

    private String age;
    private Image img2 = null;
    private Image img1 = null;
    private Image img3 = null;
    private Image img1loc = null;
    private Image img2loc = null;
    private Image img3loc = null;


    private String postId;
    private String userName;
    private String userPic;
    private String lastCommentUserName;
    private String lastCommentText;
    private String lastCommentUserId;
    private String lastCommentUserPic;
    private int userId;
    private Long timestamp;
    private String description;
    private String type;
    private Integer likeCount;
    private Integer commentCount;
    private Integer isLiked;
    private String image1;
    private String[] images;
    private String price2;
    private String price3;
    private String price1 = null;

    private List<RecommendedProduct> products;

    public String getPrice1() {
        return price1;
    }

    public void setPrice1(String price1) {
        this.price1 = price1;
    }

    public String getPrice2() {
        return price2;
    }

    public String getPrice3() {
        return price3;
    }

    public void setPrice2(String price2) {
        this.price2 = price2;
    }

    public void setPrice3(String price3) {
        this.price3 = price3;
    }


    public void setRecoProducts(List<RecommendedProduct> products) {
        this.products = products;
    }

    public List<RecommendedProduct> getRecoProducts() {
        return products;
    }


    /**
     * isFollowing : 1
     */

    private int isFollowing;
    private Integer shareCount;

    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    private boolean isSelf;

    public Integer getIsVoted() {
        return isVoted;
    }

    public void setIsVoted(Integer isVoted) {
        this.isVoted = isVoted;
    }

    private Integer isVoted;


    public String getRecommendation1() {
        return recommendation1;
    }

    public void setRecommendation1(String recommendation1) {
        this.recommendation1 = recommendation1;
    }

    public String getRecommendation2() {
        return recommendation2;
    }

    public void setRecommendation2(String recommendation2) {
        this.recommendation2 = recommendation2;
    }

    public String getRecommendation3() {
        return recommendation3;
    }

    public void setRecommendation3(String recommendation3) {
        this.recommendation3 = recommendation3;
    }

    public String getRecommendation4() {
        return recommendation4;
    }

    public void setRecommendation4(String recommendation4) {
        this.recommendation4 = recommendation4;
    }

    public List<Image> getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(List<Image> recommendation) {
        this.recommendation = recommendation;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


    public Image getImg1loc() {
        return img1loc;
    }

    public void setImg1loc(Image img1loc) {
        this.img1loc = img1loc;
    }

    public Image getImg2loc() {
        return img2loc;
    }

    public void setImg2loc(Image img2loc) {
        this.img2loc = img2loc;
    }

    public Image getImg3loc() {
        return img3loc;
    }

    public void setImg3loc(Image img3loc) {
        this.img3loc = img3loc;
    }

    public Image getImg1() {
        return img1;
    }

    public void setImg1(Image img1) {
        this.img1 = img1;
    }


    public Image getImg2() {
        return img2;
    }

    public void setImg2(Image img2) {
        this.img2 = img2;
    }


    public Image getImg3() {
        return img3;
    }

    public void setImg3(Image img3) {
        this.img3 = img3;
    }


    DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm a");

    public void setIsDesigner(Integer isDesigner) {
        this.isDesigner = isDesigner;
    }

    public Integer getIsDesigner() {
        return isDesigner;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void dataLoaded(BaseDataParser entireData) {
        if (getType().trim().equals("2"))
            setPostType(POST_TYPE_POLL);
        else if (getType().trim().equals("3"))
            setPostType(POST_TYPE_BEST_OF);
        else if (getType().trim().equals("7")) {
            setPostType(POST_RECOMMENDED_PRODS);
        } else if (getType().trim().equals("8"))
            setPostType(POST_ATC);
        else if (getType().trim().equals("11"))
            setPostType(POST_RATC);
        else if (getType().trim().equals("12")) {
            setPostType(POST_MOST_LIKED);
        }
        else if (getType().trim().equals("13")){
            setPostType(POST_TREDING_TAGS);
        }else if (getType().trim().equals("15")){
            setPostType(POST_TYPE_RECO_NEW);
        }else if (getType().trim().equals("16")) {
            setPostType(POST_TYPE_REDEMPTION);
        }
        else setPostType(POST_TYPE_NORMAL_POST);


        if (this.postType.equals(POST_RECOMMENDED_PRODS) || this.postType.equals(POST_MOST_LIKED)
                || this.postType.equals(POST_RATC) || this.postType.equals(POST_TREDING_TAGS)
                || this.postType.equals(POST_TYPE_REDEMPTION)) {
            return;
        }
        if (userId == Application.getLoginUser().id)
            isSelf = true;

        userPic = URLHelper.parseImageURL(userPic);

        if (image1 != null && image1.trim().equals("") == false)
            image1 = URLHelper.parseImageURL(image1);
        else
            image1 = null;
        if (!TextUtils.isEmpty(image1loc))
            image1loc = URLHelper.parseImageURL(image1loc);

        if (image2 != null && image2.trim().equals("") == false)
            image2 = URLHelper.parseImageURL(image2);
        else
            image2 = null;
        if (!TextUtils.isEmpty(image2loc))
            image2loc = URLHelper.parseImageURL(image2loc);

        if (image3 != null && image3.trim().equals("") == false)
            image3 = URLHelper.parseImageURL(image3);
        else
            image3 = null;
        if (!TextUtils.isEmpty(image3loc))
            image3loc = URLHelper.parseImageURL(image3loc);

        if (image1 != null)
            img1 = new Image(image1);
        if (image2 != null)
            img2 = new Image(image2);
        if (image3 != null)
            img3 = new Image(image3);

        if (!TextUtils.isEmpty(image1loc))
            img1loc = new Image(image1loc);
        if (!TextUtils.isEmpty(image2loc))
            img2loc = new Image(image2loc);
        if (!TextUtils.isEmpty(image3loc))
            img3loc = new Image(image3loc);



        /*Date date = (new Date(Long.parseLong(timestamp.toString())));
        age = sdf.format(date);

        age = age.replace(",",", at ");*/
        String result = SmartTimeAgo.getSmartTime(/*null, */timestamp/*, true*/);
        /*if (result.equals(SmartTimeAgo.SMART_TIME_EOF))
            setDateAsEarlier();
        else */
        age = result;

        //strElapsed(timestamp);


        //recommendation1=recommendation2=recommendation3=recommendation4=oldData;

        recommendation = new ArrayList<>();
        recommendation1 = URLHelper.parseImageURL(recommendation1);
        recommendation2 = URLHelper.parseImageURL(recommendation2);
        recommendation3 = URLHelper.parseImageURL(recommendation3);
        recommendation4 = URLHelper.parseImageURL(recommendation4);

        if (getRecommendation1() != null && getRecommendation1().equals("") == false)
            recommendation.add(new Image(getRecommendation1()));

        if (getRecommendation2() != null && getRecommendation2().equals("") == false)
            recommendation.add(new Image(getRecommendation3()));

        if (getRecommendation3() != null && getRecommendation3().equals("") == false)
            recommendation.add(new Image(getRecommendation3()));

        if (getRecommendation4() != null && getRecommendation4().equals("") == false)
            recommendation.add(new Image(getRecommendation4()));

        if (recommendation.size() == 0)
            recommendation = null;

    }


    public String getImage1loc() {
        return image1loc;
    }

    public void setImage1loc(String image1loc) {
        this.image1loc = image1loc;
    }

    public String getImage2loc() {
        return image2loc;
    }

    public void setImage2loc(String image2loc) {
        this.image2loc = image2loc;
    }

    public String getImage3loc() {
        return image3loc;
    }

    public void setImage3loc(String image3loc) {
        this.image3loc = image3loc;
    }

    private void setDateAsEarlier() {
        Date date = (new Date(Long.parseLong(timestamp.toString())));
        age = sdf.format(date);

        age = age.replace(",", ", at ");
    }

    public String strElapsed(Long timestamp) {
        Long difference = new Date().getTime() - timestamp;
        long seconds;
        long minutes;
        long hours;
        long days;
        long x = difference / 1000;
        seconds = x % 60;
        x /= 60;
        minutes = x % 60;
        x /= 60;
        hours = x % 24;
        x /= 24;
        days = x;
        if (days != 0)
            return "d" + days + " h" + hours + " m" + minutes + " s" + seconds;
        else if (hours != 0)
            return " h" + hours + " m" + minutes + " s" + seconds;
        else if (minutes != 0)
            return " m" + minutes + " s" + seconds;
        else
            return " m0  s" + seconds;
    }


    public Integer getPostType() {
        return postType;
    }

    public void setPostType(Integer postType) {
        this.postType = postType;
    }

    private Integer postType;

    private String image2;


    private String image3;
    private Integer voteCount;
    private Integer yesPercent;
    private Integer noPercent;


    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }


    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPic(String userPic) {

        this.userPic = userPic;
    }

    public void setLastCommentUserName(String lastCommentUserName) {
        this.lastCommentUserName = lastCommentUserName;
    }

    public void setLastCommentText(String lastCommentText) {
        this.lastCommentText = lastCommentText;
    }

    public void setLastCommentUserId(String lastCommentUserId) {
        this.lastCommentUserId = lastCommentUserId;
    }

    public void setLastCommentUserPic(String lastCommentUserPic) {
        this.lastCommentUserPic = lastCommentUserPic;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public void setIsLiked(Integer isLiked) {
        this.isLiked = isLiked;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public void setYesPercent(Integer yesPercent) {
        this.yesPercent = yesPercent;
    }

    public void setNoPercent(Integer noPercent) {
        this.noPercent = noPercent;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getLastCommentUserName() {
        return lastCommentUserName;
    }

    public String getLastCommentText() {
        return lastCommentText;
    }

    public String getLastCommentUserId() {
        return lastCommentUserId;
    }

    public String getLastCommentUserPic() {
        return lastCommentUserPic;
    }

    public int getUserId() {
        return userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public Integer getIsLiked() {
        return isLiked;
    }


    public String getImage1() {
        return image1;
    }

    public String[] getImages() {
        if (images == null) {
            images = new String[3];
            images[0] = image1;
            images[1] = image2;
            images[2] = image3;
        }
        return images;
    }

    public int[] getVotes() {
        int[] votes = new int[3];

        if (getPostType() == POST_TYPE_POLL) {
            votes[0] = getYesPercent();
            votes[1] = getNoPercent();
            votes[2] = -1;
        } else if (getPostType() == POST_TYPE_BEST_OF) {
            votes[0] = getBof3Percent1();
            votes[1] = getBof3Percent2();
            if (getImg1() != null)
                votes[2] = getBof3Percent3();
            else
                votes[2] = -1;
        }
        return votes;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Integer getYesPercent() {
        return yesPercent;
    }

    public Integer getNoPercent() {
        return noPercent;
    }

    public void setBof3Percent1(Integer bof3Percent1) {
        this.bof3Percent1 = bof3Percent1;
    }

    public void setBof3Percent2(Integer bof3Percent2) {
        this.bof3Percent2 = bof3Percent2;
    }

    public void setBof3Percent3(Integer bof3Percent3) {
        this.bof3Percent3 = bof3Percent3;
    }

    public Integer getBof3Percent1() {
        return bof3Percent1;
    }

    public Integer getBof3Percent2() {
        return bof3Percent2;
    }

    public Integer getBof3Percent3() {
        return bof3Percent3;
    }

    public void setIsFollowing(int isFollowing) {
        this.isFollowing = isFollowing;
    }

    public int getIsFollowing() {
        return isFollowing;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

}
