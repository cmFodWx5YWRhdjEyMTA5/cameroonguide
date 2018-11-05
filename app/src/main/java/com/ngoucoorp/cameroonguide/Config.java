package com.ngoucoorp.cameroonguide;

import android.app.Application;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class Config extends Application{

    public static final String GOOGLE_PROJECT_NO = "312984937906";

    public static final Boolean SHOW_APMOB = true;

    public static final String MSG_KEY = "m";

    public static final int PAGINATION = 3;





    public static final String APP_API_URL = "http://orangeapp.ifcad.info/citiesdirectory/index.php";

    public static final String APP_IMAGES_URL = "http://orangeapp.ifcad.info/citiesdirectory/uploads/";


/*
    public static final String APP_API_URL = "http://192.168.1.185/citiesdirectory/index.php";

    public static final String APP_IMAGES_URL = "http://192.168.1.185/citiesdirectory/uploads/";

*/
    public static final String GET_ALL = "/rest/cities/get";

    public static final String ITEMS_BY_SUB_CATEGORY = "/rest/items/get/city_id/";

    public static final String ITEMS_BY_ID = "/rest/items/get/id/";

    public static final String SEARCH_BY_GEO = "/rest/items/search_by_geo/miles/";

    public static final String POST_ITEM_INQUIRY = "/rest/items/inquiry/id/";

    public static final String POST_USER_LOGIN = "/rest/appusers/login";

    public static final String POST_REVIEW = "/rest/items/review/id/";

    public static final String POST_USER_REGISTER = "/rest/appusers/add/";

    public static final String PUT_USER_UPDATE = "/rest/appusers/update/id/";

    public static final String POST_ITEM_LIKE = "/rest/items/like/id/";

    public static final String POST_ITEM_FAVOURITE = "/rest/items/favourite/id/";

    public static final String POST_ITEM_SEARCH = "/rest/items/search/city_id/";

    public static final String POST_TOUCH_COUNT = "/rest/items/touch/id/";

    public static final String POST_PROFILE_IMAGE = "/rest/images/upload";

    public static final String POST_FCM_REGISTER = "/rest/gcm/register";

    public static final String POST_FCM_UNREGISTER = "/rest/gcm/unregister";

    public static final String POST_ITEM_RATING = "/rest/items/rating/id/";

    public static final String POST_ITEM_IS_RATE = "/rest/items/is_rate/id/";

    public static final String GET_FAVOURITE = "/rest/items/is_favourite/id/";

    public static final String GET_LIKE = "/rest/items/is_like/id/";

    public static final String GET_FORGOT_PASSWORD = "/rest/appusers/reset/email/";

    public static final String GET_FAVOURITE_ITEMS = "/rest/items/user_favourites/user_id/";

    public static final String GET_CITY_NEWS = "/rest/cities/feeds/city_id/";

    public static final String GET_ABOUT = "/rest/abouts/index";


}
