package com.ynyx.epic.xpush.smack;

import com.ynyx.epic.xpush.utils.L;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * XmppManager tool  class 
 */
public class XmppManager {

    private static XmppManager mInstance = new XmppManager();

    private XMPPTCPConnection connection = null;

    /**
     * single
     *
     * @return
     */
    synchronized public static XmppManager getInstance() {
        return mInstance;
    }


    public XMPPTCPConnection getConnection() {
        if (connection == null || !connection.isConnected()) {
            return null;
        }
        return connection;
    }

    /**
     * open openfire connection
     *
     * @param host
     * @param serverName  
     * @param port
     * @param resourceTag
     * @return
     */
    public XMPPTCPConnection openConnection(String host, String serverName, int port, String resourceTag) {
        L.d(" openConnection() ");
        try {
            if (null == connection || !connection.isAuthenticated()) {
                XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration
                        .builder()
                        .setResource(resourceTag)
                        .setServiceName(serverName)
                        .setHost(host)
                        .setSendPresence(false)
                        .setPort(port)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setCompressionEnabled(false)
                        .setConnectTimeout(10 * 1000)
                        .setDebuggerEnabled(L.isPrintDebug);
                // TODO: 2016/4/22 
                // SmackConfiguration.setDefaultPingInterval(30);
                SmackConfiguration.setDefaultPacketReplyTimeout(8 * 1000);
                connection = new XMPPTCPConnection(configBuilder.build());
                connection.connect();//
//                 configureConnection(ProviderManager.getInstance());

            }
        } catch (Exception e) {
            L.e("xmppmanager  connection falied");
            e.printStackTrace();
            connection = null;
        }
        return connection;
    }

    /**
     * disconnect connection
     */
    public void closeConnection() {
        if (connection != null) {
            // 移除连接监听
//			 connection.removeConnectionListener(connectionListener);
            try {
                connection.disconnect();
                connection.instantShutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection = null;
        }
        L.d("XmppManager", "关闭连接");
    }
}
