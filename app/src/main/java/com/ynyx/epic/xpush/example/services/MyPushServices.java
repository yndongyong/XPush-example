package com.ynyx.epic.xpush.example.services;

import com.ynyx.epic.xpush.example.Utils.DevicesUtils;
import com.ynyx.epic.xpush.example.listener.TestPushMessageFilter;
import com.ynyx.epic.xpush.example.listener.TestPushMessageListener;
import com.ynyx.epic.xpush.example.store.ConstDefine;
import com.ynyx.epic.xpush.service.PushService;


/**
 * Created by Dong on 2016/4/20.
 */
public class MyPushServices extends PushService {

    @Override
    public void onCreate() {
        super.onCreate();
        addPushMessageListener(new TestPushMessageListener(this),
                new TestPushMessageFilter());
    }

    @Override
    protected String getLocalUserName() {
        return ConstDefine.username;
    }

    @Override
    protected String getLocalUserPwd() {
        return ConstDefine.userpwd;
    }

    @Override
    protected String getLocalUserNick() {
        return ConstDefine.usernickname;
    }

    @Override
    protected String getLocalUserEmail() {
        return ConstDefine.userEmail;
    }


    @Override
    protected boolean isLogout() {
        return false;
    }

    @Override
    protected String getPushServerHost() {
        return ConstDefine.PUSH_SERVER_HOST;
    }

    @Override
    protected int getPushServerPort() {
        return ConstDefine.PUSH_SERVER_PORT;
    }


    @Override
    protected String getPushServiceName() {
        return ConstDefine.PUSH_SERVER_NAME;
    }

    @Override
    protected String getResourceTag() {
        return DevicesUtils.getAndroidDeviceId(this);
    }
    
}
