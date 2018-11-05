package com.ngoucoorp.cameroonguide;

import com.ngoucoorp.cameroonguide.models.PCityData;
import com.ngoucoorp.cameroonguide.models.PItemData;
import com.ngoucoorp.cameroonguide.models.PNewsData;

import java.util.ArrayList;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */


public class GlobalData {
    public static PItemData itemData =  null;
    public static PCityData citydata = null;
    public static ArrayList<PCityData> cityDatas = new ArrayList<PCityData>();
    public static  ArrayList<PNewsData> filterednews = new ArrayList<PNewsData>();

    public static  PNewsData notifData = new PNewsData();
}
