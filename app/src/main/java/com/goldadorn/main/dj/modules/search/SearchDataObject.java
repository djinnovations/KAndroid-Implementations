package com.goldadorn.main.dj.modules.search;

import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.utils.ImageFilePath;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;

import java.util.List;

/**
 * Created by User on 21-10-2016.
 */
public class SearchDataObject {


    public static class ProductSearchData implements IParseableObject {

        private int productId;
        private String prodDesc;
        private String sku;
        private String productLabel;
        private long productPrice;
        private String productDesc;
        private int desgnId;
        private String desgnName;
        private String imageUrl;

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getProdDesc() {
            return prodDesc;
        }

        public void setProdDesc(String prodDesc) {
            this.prodDesc = prodDesc;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getProductLabel() {
            return productLabel;
        }

        public void setProductLabel(String productLabel) {
            this.productLabel = productLabel;
        }

        public long getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(long productPrice) {
            this.productPrice = productPrice;
        }

        public String getProductDesc() {
            return productDesc;
        }

        public void setProductDesc(String productDesc) {
            this.productDesc = productDesc;
        }

        public int getDesgnId() {
            return desgnId;
        }

        public void setDesgnId(int desgnId) {
            this.desgnId = desgnId;
        }

        public String getDesgnName() {
            return desgnName;
        }

        public void setDesgnName(String desgnName) {
            this.desgnName = desgnName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public void dataLoaded(BaseDataParser baseDataParser) {
            imageUrl = ImageFilePath.getImageUrlForProduct(desgnId, productId, null, false, 200);
        }
    }

    public static class CollectionSearchData implements IParseableObject {

        private int collectionId;
        private String collectionTitle;
        private String collectionCategory;
        private String collectionDesc;
        private int collectionProductCount;
        private int desgnId;
        private String desgnName;

        private String imageUrl;

        public int getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(int collectionId) {
            this.collectionId = collectionId;
        }

        public String getCollectionTitle() {
            return collectionTitle;
        }

        public void setCollectionTitle(String collectionTitle) {
            this.collectionTitle = collectionTitle;
        }

        public String getCollectionCategory() {
            return collectionCategory;
        }

        public void setCollectionCategory(String collectionCategory) {
            this.collectionCategory = collectionCategory;
        }

        public String getCollectionDesc() {
            return collectionDesc;
        }

        public void setCollectionDesc(String collectionDesc) {
            this.collectionDesc = collectionDesc;
        }

        public int getCollectionProductCount() {
            return collectionProductCount;
        }

        public void setCollectionProductCount(int collectionProductCount) {
            this.collectionProductCount = collectionProductCount;
        }

        public int getDesgnId() {
            return desgnId;
        }

        public void setDesgnId(int desgnId) {
            this.desgnId = desgnId;
        }

        public String getDesgnName() {
            return desgnName;
        }

        public void setDesgnName(String desgnName) {
            this.desgnName = desgnName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        @Override
        public void dataLoaded(BaseDataParser baseDataParser) {
            imageUrl = Collection.getImageUrl(desgnId, collectionId);
        }
    }

    public static class UserSearchData implements IParseableObject {
        private String userid;
        private String username;
        private String lastname;
        private String profilePic;
        private int isFollow;


        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public int getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(int isFollow) {
            this.isFollow = isFollow;
        }

        @Override
        public void dataLoaded(BaseDataParser baseDataParser) {
            profilePic = URLHelper.parseImageURL(profilePic);
        }
    }

    private int usersOffset;
    private int designersOffset;
    private int collOffset;
    private int prodOffset;
    private List<UserSearchData> users;
    private List<UserSearchData> designers;
    private List<CollectionSearchData> collList;
    private List<ProductSearchData> prodList;


    public int getCollOffset() {
        return collOffset;
    }

    public void setCollOffset(int collOffset) {
        this.collOffset = collOffset;
    }

    public int getProdOffset() {
        return prodOffset;
    }

    public void setProdOffset(int prodOffset) {
        this.prodOffset = prodOffset;
    }

    public List<CollectionSearchData> getCollList() {
        return collList;
    }

    public void setCollList(List<CollectionSearchData> collList) {
        this.collList = collList;
    }

    public List<ProductSearchData> getProdList() {
        return prodList;
    }

    public void setProdList(List<ProductSearchData> prodList) {
        this.prodList = prodList;
    }

    public int getUsersOffset() {
        return usersOffset;
    }

    public void setUsersOffset(int usersOffset) {
        this.usersOffset = usersOffset;
    }

    public int getDesignersOffset() {
        return designersOffset;
    }

    public void setDesignersOffset(int designersOffset) {
        this.designersOffset = designersOffset;
    }

    public List<UserSearchData> getUsers() {
        return users;
    }

    public void setUsers(List<UserSearchData> users) {
        this.users = users;
    }

    public List<UserSearchData> getDesigners() {
        return designers;
    }

    public void setDesigners(List<UserSearchData> designers) {
        this.designers = designers;
    }


    public void onLoadProducts() {
        if (getProdList() == null)
            return;
        for (ProductSearchData data : getProdList()) {
            data.dataLoaded(null);
        }
    }


    public void onLoadColl() {
        if (getCollList() == null)
            return;
        for (CollectionSearchData data : getCollList()) {
            data.dataLoaded(null);
        }
    }


    public void onLoadDes() {
        if (getDesigners() == null)
            return;
        for (UserSearchData data : getDesigners()) {
            data.dataLoaded(null);
        }
    }

}
