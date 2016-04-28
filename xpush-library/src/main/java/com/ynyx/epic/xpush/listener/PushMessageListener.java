package com.ynyx.epic.xpush.listener;



import com.ynyx.epic.xpush.vo.Msg;

import java.util.List;

/**
 * processor push message
 */
public interface PushMessageListener {

    void processOnlineMessage(Msg message);

    void processOfflineMessage(List<Msg> messages);
}
