package com.goldadorn.main.sharedPreferences;

/**
 * Created by bhavinpadhiyar on 8/12/15.
 */
public class AppSharedPreferences {
    public class LoginInfo
    {
        public static final String NAME ="loginInfo";
        public static final String IS_INTRO_SEEN ="introSeen";
        public static final String USER_NAME ="userName";
        public static final String PASSWORD ="password";
        public static final String USER_ID ="userid";
        public static final String IS_LOGIN_DONE ="isLoginDone";

        //Author DJphy
        public static final String IS_SOCIAL_LOGIN = "social_login";
        public static final String SOCIAL_LOGIN_PLATFORM = "login_platform";

        //public static final String SELECTED_CITY ="selectedCityName";
        //public static final String SELECTED_CITY_ID ="selectedCityId";
    }
    public class AppRater
    {
        public final static String PREF_NAME = "apprater";
        public final static int DAYS_UNTIL_PROMPT = 5;
        public final static int LAUNCHES_UNTIL_PROMPT = 5;
        //public final static int DAYS_UNTIL_PROMPT_FOR_REMIND_LATER = 3;
        //public final static int LAUNCHES_UNTIL_PROMPT_FOR_REMIND_LATER = 7;
    }
}
