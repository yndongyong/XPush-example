package com.ynyx.epic.xpush.example.listener;

import android.content.Context;

import com.ynyx.epic.xpush.example.Utils.NotifyUtils;
import com.ynyx.epic.xpush.listener.PushMessageListener;
import com.ynyx.epic.xpush.vo.Msg;


import java.util.List;

/**
 * Created by Dong on 2016/4/20.
 */
public class TestPushMessageListener implements PushMessageListener {

    private Context mContext;
    
    public TestPushMessageListener(Context _context){
        this.mContext = _context;
    }
    
    //处理在线消息
    @Override
    public void processOnlineMessage(Msg message) {
        //发送一个通知
        String body = message.getBody();
        NotifyUtils.showNotification(mContext,"在线消息",body);
    }
    //处理连线消息
    @Override
    public void processOfflineMessage(List<Msg> messages) {
        //发送一个通知
        StringBuilder builder = new StringBuilder();
        builder.append("接收到了 ").append(messages.size()).append(" 条消息");
        NotifyUtils.showNotification(mContext,"离线消息",builder.toString());
        
    }
}
