package com.ynyx.epic.xpush.example.listener;

import com.ynyx.epic.xpush.listener.PushMessageFilter;
import com.ynyx.epic.xpush.vo.Msg;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dong on 2016/4/20.
 */
public class TestPushMessageFilter implements PushMessageFilter {
    //过滤不满足条件的在线消息
    @Override
    public boolean acceptOnlineMessage(Msg message) {
        if (message != null) {
            if (message.getBody() != null) {
                return true;
            }
        }
        return false;
    }
    //过滤不满足条件的离线消息
    @Override
    public boolean acceptOfflineMessage(List<Msg> messages) {
        ArrayList<Msg> list = this.getAcceptOfflineMessage(messages);
        if (list != null || list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Msg> getAcceptOfflineMessage(List<Msg> messages) {
        ArrayList<Msg> list = new ArrayList<Msg>();
        for (Msg msg : messages) {
            if (this.acceptOnlineMessage(msg)) {
                list.add(msg);
            }
        }
        return list;
    }
}
