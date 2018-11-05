package com.ngoucoorp.cameroonguide.utilities;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Utils.psLog("token : " + token);

    }

}
