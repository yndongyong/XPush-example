package com.ynyx.epic.xpush.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.text.TextUtils;

import com.ynyx.epic.xpush.listener.PushMessageFilter;
import com.ynyx.epic.xpush.listener.PushMessageListener;
import com.ynyx.epic.xpush.smack.XmppManager;
import com.ynyx.epic.xpush.utils.L;
import com.ynyx.epic.xpush.utils.NetWorkUtils;
import com.ynyx.epic.xpush.vo.Msg;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dong on 2015/11/23.
 */
@SuppressLint("NewApi")
public abstract class PushService extends Service{

    public static final String TAG = PushService.class.getSimpleName();

    private XMPPTCPConnection mConnection;
    private MyConnectionListener mConnectionListener;

    /**
     * open  connection 
     *
     * @param context
     */
    public static void startService(Context context) {
        L.d(TAG, "startService() ");
        Intent intent = getXmppServiceIntent(context);
        if (intent != null) {
            context.startService(intent);
        }
    }

    /**
     * 停止
     *
     * @param context
     */
    public static void stopService(Context context) {
        Intent intent = getXmppServiceIntent(context);
        if (intent != null) {
            context.stopService(intent);
           
        }
    }

    private static Intent getXmppServiceIntent(Context context) {
        Intent intent = new Intent(PushService.class.getName());
        L.d(TAG, PushService.class.getName());
        intent.setPackage(context.getPackageName());

        List<ResolveInfo> resolveInfos = context.getPackageManager()
                .queryIntentServices(intent, 0);

        L.d(TAG, "List<ResolveInfo>:" + resolveInfos);

        if (resolveInfos != null) {
            for (ResolveInfo info : resolveInfos) {
                Intent i = new Intent();
                i.setClassName(info.serviceInfo.packageName,
                        info.serviceInfo.name);
                return i;
            }
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        L.d(TAG, "onCreate() ");
        startServiceRepeatly(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d(TAG, "onStartCommand() " + this);
        final Context context = this;

        dealXmpp(this);

        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private String mTagLocalOld;

    private void dealXmpp(final Context context) {

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                L.d(TAG, "onStartCommand() isXmppConnectionRunning():"
                        + isXmppConnectionRunning());

                boolean isNetworkAvailable = NetWorkUtils
                        .isNetworkAvailable(context);
                L.d(TAG, "onStartCommand() isNetworkAvailable:"
                        + isNetworkAvailable);

                String userName = getLocalUserName();
                String userPwd = getLocalUserPwd();

                String openfireHost = getPushServerHost();
                int port = getPushServerPort();
                String serviceName = getPushServiceName();
                String openfireResourceTag = getResourceTag();

                L.d(TAG, "onStartCommand() tagLocalNew:" + userName);

                String tagLocalOld = mTagLocalOld;
                mTagLocalOld = userName;

                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)
                        || !isNetworkAvailable || !tagLocalOld.equals(userName)) {
                    destroyXmppConnection();
                }

                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPwd)
                        && !isXmppConnectionRunning() && isNetworkAvailable) {

                    destroyXmppConnection();

                    XmppConnectionInit(userName, userPwd, openfireHost, serviceName, port,
                            openfireResourceTag);
                    // regesiter online message listener
                    registerOnlineMessageListener();

                }
            }
        });
    }
 
    private void startServiceRepeatly(Context context) {
        Intent intent = getXmppServiceIntent(context);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent mPendingIntent = PendingIntent.getService(this, 0,
                // intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long now = System.currentTimeMillis();
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 20 * 1000,
                mPendingIntent);
    }

    /**
     * check the push services is available 
     *
     * @return
     */
    private boolean isXmppConnectionRunning() {
        if (mConnection != null) {
            L.d(TAG, "connection  isConnected:" + mConnection.isConnected());

        } else {
            L.d(TAG, "connection is null");
        }

        // connection is normal and servers is accessable
        return mConnection != null && mConnection.isConnected() /*
                                                                 * &&
																 * checkPingPong
																 * ()
																 */
                && mConnection.isAuthenticated();
    }

    /**
     * destory push service
     */
    private void destroyXmppConnection() {
        L.d(TAG, "destroyXmppConnection()");
        try {
            if (mConnection != null) {
                mConnection.disconnect();
                mConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init push services and then to login
     *
     * @param username
     * @param userPwd
     */
    private void XmppConnectionInit(String username, String userPwd, String host, String serverName,
                                    int port, String resourceTag) {
        L.d(TAG, "XmppConnectionInit");
        L.d(TAG, "username:" + username);
        L.d(TAG, "userPwd:" + userPwd);
        mConnection = 
                XmppManager.getInstance().openConnection(host, serverName, port, resourceTag);
        if (mConnection == null || !mConnection.isConnected()) {
            return;
        }
        try {
            mConnection.login(username, userPwd, getResourceTag());
            // add connection listener
            mConnectionListener = new MyConnectionListener();
            mConnection.addConnectionListener(mConnectionListener);

            // dela with offline message
            List<Message> messages = getOfflineMessage();
            // set online status presence
            Presence presence = new Presence(Presence.Type.available);
            mConnection.sendStanza(presence);
            if (messages.size() > 0) {
                PushService.this.onOfflineMessage(messages);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof org.jivesoftware.smack.sasl.SASLErrorException) {

                try {
                    register(username, userPwd, getLocalUserNick(), getLocalUserEmail());
                    mConnection.login(username, userPwd, getResourceTag());

                    mConnectionListener = new MyConnectionListener();
                    mConnection.addConnectionListener(mConnectionListener);

                    List<Message> messages = getOfflineMessage();
                    Presence presence = new Presence(Presence.Type.available);
                    mConnection.sendStanza(presence);
                    if (messages.size() > 0) {
                        PushService.this.onOfflineMessage(messages);
                    }
                } catch (SmackException.NotConnectedException e1) {
                    e1.printStackTrace();
                } catch (XMPPException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (SmackException e1) {
                    // TODO: 2016/4/20  XMPPError: conflict - cancel 
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (e instanceof SmackException.NoResponseException) {
                // TODO: 2016/4/15 server  no  response 
                XmppManager.getInstance().closeConnection();
                mConnection = null;
            }
        }

    }

    private void register(String uname, String upassword, String nickname, String email) throws
            Exception {
        AccountManager acc = AccountManager.getInstance(mConnection);
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("name", nickname);
        attributes.put("email", email);
        acc.createAccount(uname, upassword, attributes);
    }

    public List<Message> getOfflineMessage() {
        if (mConnection == null)
            return null;
        List<Message> offlineMsgs = new ArrayList<Message>();
        try {
            OfflineMessageManager om = new OfflineMessageManager(mConnection);
            
			if (om.getMessageCount() == 0) {
                return offlineMsgs;
            }
            List<Message> messages = om.getMessages();
            for (Message m : messages) {
                if (m.getBody() != null) {
                    offlineMsgs.add(m);
                }
            }
            om.deleteMessages();
            return offlineMsgs;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            // TODO: 2016/4/18 conflict error 
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return offlineMsgs;
    }

    /**
     * ping  pong 
     *
     * @return
     */
    public boolean checkPingPong() {
        if (mConnection == null) {
            return false;
        }
        try {
            PingManager pingManager = PingManager.getInstanceFor(mConnection);
            if (pingManager == null) {
                return false;
            }
//            pingManager.setPingInterval(45);
            pingManager.setPingInterval(-1);
            return pingManager.pingMyServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * register  online  message listener 
     */
    public void registerOnlineMessageListener() {
        L.d(TAG, "registerOnlineMessageListener");
        StanzaFilter filter = new StanzaTypeFilter(Message.class);
        StanzaListener listener = new StanzaListener() {

            @Override
            public void processPacket(Stanza packet)
                    throws SmackException.NotConnectedException {
                if (packet instanceof Message) {
                    Message msg = (Message) packet;
                    if (!TextUtils.isEmpty(msg.getBody())) {
                        L.e(TAG, " Message: " + msg.toXML());
                        PushService.this.onOnlineMessage(msg);
                    }
                }
            }
        };
        mConnection.addSyncStanzaListener(listener, filter);
    }

    /**
     * username@skeysea.com
     * username
     * @return
     */
    protected abstract String getLocalUserName();

    /**
     */
    protected abstract String getLocalUserPwd();

    /**
     *
     * @return
     */
    protected abstract String getLocalUserNick();

    /**
     *
     * @return
     */
    protected abstract String getLocalUserEmail();

    /**
     *
     * @return
     */
    protected abstract boolean isLogout();

    /**
     *
     * @return
     */
    protected abstract String getPushServerHost();

    /**
     *
     * @return
     */
    protected abstract int getPushServerPort();

    /**
     *
     * @return
     */
    protected abstract String getPushServiceName();

    /**
     *
     * @return
     */
    protected abstract String getResourceTag();


    /**
     * @param message
     */
    protected void onOnlineMessage(Message message) {
        L.d(TAG, "onOnlineMessage");
        executorService.submit(new ListenerNotification(message));
    }

    /**
     * 
     * @param messages
     */
    protected void onOfflineMessage(List<Message> messages) {
        L.d(TAG, "onOfflineMessage size:" + messages.size());
        executorService.submit(new ListenerNotification(messages));
    }


    protected final Map<PushMessageListener, ListenerWrapper> recvListeners = new ConcurrentHashMap<PushMessageListener, ListenerWrapper>();

    public void addPushMessageListener(PushMessageListener msgListener,
                                       PushMessageFilter msgFilter) {
        if (msgListener == null) {
            throw new NullPointerException("Packet listener is null.");
        }
        ListenerWrapper wrapper = new ListenerWrapper(msgListener, msgFilter);
        recvListeners.put(msgListener, wrapper);
    }

    protected static class ListenerWrapper {

        private PushMessageListener packetListener;
        private PushMessageFilter packetFilter;

        public ListenerWrapper(PushMessageListener packetListener,
                               PushMessageFilter packetFilter) {
            this.packetListener = packetListener;
            this.packetFilter = packetFilter;
        }

        public void notifyListenerOnlineMessage(Message message) {
            // TODO: 2016/4/22   
            Msg msg = null;
            try {
                msg = retireMsgFromMessage(message);
                if (packetFilter == null
                        || packetFilter.acceptOnlineMessage(msg)) {
                    packetListener.processOnlineMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void notifyListenerOfflineMessage(List<Message> messages) {
            List<Msg> msgs = new ArrayList<Msg>();
            for (Message message : messages) {
                try {
                    Msg msg = retireMsgFromMessage(message);
                    msgs.add(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (packetFilter == null
                    || packetFilter.acceptOfflineMessage(msgs)) {
                packetListener.processOfflineMessage(packetFilter
                        .getAcceptOfflineMessage(msgs));
            }
        }

        private  Msg retireMsgFromMessage(Message message) {
            Msg msg = new Msg();
            msg.setFrom(message.getFrom());
            msg.setTo(message.getTo());
            msg.setId(message.getStanzaId());
            msg.setType(message.getType().toString());
            msg.setBody(message.getBody());
            return msg;
        }
    }

    

    private class ListenerNotification implements Runnable {

        private Message message;
        private List<Message> messages;

        public ListenerNotification(Message message) {
            L.d(TAG, " ListenerNotification");
            this.message = message;
        }

        public ListenerNotification(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            L.d(TAG, " recvListeners size:" + recvListeners.size());
            for (ListenerWrapper listenerWrapper : recvListeners.values()) {

                if (message != null) {
                    try {
                        listenerWrapper.notifyListenerOnlineMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (messages != null) {
                    try {
                        listenerWrapper.notifyListenerOfflineMessage(messages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private final static AtomicInteger connectionCounter = new AtomicInteger(0);

    private final int connectionCounterValue = connectionCounter
            .getAndIncrement();

    // private final ScheduledExecutorService executorService = new
    // ScheduledThreadPoolExecutor(2,
    // new SmackExecutorThreadFactory(connectionCounterValue));
    protected final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(
            1, new SmackExecutorThreadFactory(connectionCounterValue));

    private static final class SmackExecutorThreadFactory implements
            ThreadFactory {
        private final int connectionCounterValue;
        private int count = 0;

        private SmackExecutorThreadFactory(int connectionCounterValue) {
            this.connectionCounterValue = connectionCounterValue;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "Smack Executor Service "
                    + count++ + " (" + connectionCounterValue + ")");
            thread.setDaemon(true);
            return thread;
        }
    }

    /**
     * connection  listener 
     */
    class MyConnectionListener implements ConnectionListener {

        @Override
        public void connected(XMPPConnection connection) {

        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {

        }

        @Override
        public void connectionClosed() {
            L.d(TAG, " connectionClosed()");
            L.d(TAG, " connection status:" + mConnection.isConnected());
            L.d(TAG, " connectionClosed() isLogout:" + isLogout());
            if (isLogout()) {
                XmppManager.getInstance().closeConnection();
            } else {
                try {
                    String pwd = getLocalUserPwd();
                    pwd = "1";
                    XmppConnectionInit(getLocalUserName(), pwd, getPushServerHost(),
                            getPushServiceName(), getPushServerPort(), getResourceTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            L.d(TAG, " connectionClosedOnError()");
            e.printStackTrace();

            if (e instanceof XMPPException.StreamErrorException) {
                String str = "conflict You can read more about the meaning of this stream error at http://xmpp.org/rfcs/rfc6120.html#streams-error-conditions";
                if (e.getMessage().toString().equalsIgnoreCase(str)) {
                    // TODO: 2016/3/31  conflict  error 
                }
            }
            
            try {
                XmppConnectionInit(getLocalUserName(), getLocalUserPwd(), getPushServerHost(),
                        getPushServiceName(), getPushServerPort(), getResourceTag());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void reconnectionSuccessful() {
            L.d(TAG, " reconnectionSuccessful()");
        }

        @Override
        public void reconnectingIn(int seconds) {
            L.d(TAG, " reconnectingIn()");
        }

        @Override
        public void reconnectionFailed(Exception e) {
            L.d(TAG, " reconnectionFailed()");
        }
    }
}
