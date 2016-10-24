package com.goldadorn.main.dj.model;

import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;

import java.util.List;

/**
 * Created by User on 21-10-2016.
 */
public class SearchDataObject {

    public class UserSearchData {
        String userid;
        String username;
        String lastname;
        String profilePic;
        int isFollow;


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
    }

    private int usersOffset;
    private int designersOffset;
    private List<UserSearchData> users;
    private List<UserSearchData> designers;

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

}
