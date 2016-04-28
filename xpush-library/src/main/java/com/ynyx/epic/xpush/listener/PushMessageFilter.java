package com.ynyx.epic.xpush.listener;


import com.ynyx.epic.xpush.vo.Msg;

import java.util.ArrayList;
import java.util.List;

/**
 * to save or send broadcast or send notifycation
 */
public interface PushMessageFilter {

    /**
     * Whether or not to receive online messages
     * @param message
     * @return
     */
    boolean acceptOnlineMessage(Msg message);

    /**
     * Whether or not to receive offline messages
     * @param messages
     * @return
     */
    boolean acceptOfflineMessage(List<Msg> messages);

    /**
     * get the offline message
     * @param messages
     * @return
     */
    ArrayList<Msg> getAcceptOfflineMessage(List<Msg> messages);
}
