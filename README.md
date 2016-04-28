# XPush-example
## XPush android sdk 集成指南 ##
### 使用提示 ###

本文是 Android SDK 标准的集成指南文档。

匹配的 SDK 版本为：v1.0.0及以后版本。

### 产品功能说明 ###
本推送（XPush）是一个端到端的推送服务，使得服务器端消息能够及时地推送到终端用户手机上，让开发者积极地保持与用户的连接，从而提高用户活跃度、提高应用的留存率。

本 Android SDK 方便开发者基于 XPush 来快捷地为 Android App 增加推送功能。

### 主要功能 ###
 * 保持与服务器的长连接，以便消息能够即时推送到达客户端
 * 接收自定义消息，并向开发者App 传递相关推送信息	

### 主要特点 ###
 * 客户端维持连接占用资源少、耗电低
 * SDK丰富的接口，可定制通知栏提示样式
 * 服务器部署于内网，大容量且稳定
 
### Android SDK 版本 ###
目前SDK支持Android 2.3或以上版本的手机系统(支持android 6.0系统)。

## SDK集成步骤 ##
### 导入 SDK 开发包到你自己的应用程序项目 ###
* 解压缩 XPush-sdk_v1.0.0.zip 压缩包
* 复制 libs/xpush-sdk-v1.0.0.arr 到工程 libs/ 目录下
* 配置模块工程的build.gradle文件，加入如下配置

	     repositories {
    		flatDir {
    		dirs 'libs' //this way we can find the .aar file in libs folder
    		}
    	} 
      
* 在模块工程的build.gradle文件下dependencies节点中通过如下方式引用

	`compile(name: "xpush-sdk-v1.0.aar", ext: "aar")`





### 配置 AndroidManifest.xml ###
可根据 demo工程中的 AndroidManifest.xml 样例文件，来配置应用程序项目的 AndroidManifest.xml。

主要步骤为：

* 复制备注为 "Required" 的部分
* 将备注为替换包名的部分，替换为当前应用程序的包名
* 将AppKey替换为在移动应用管理平台上注册该应用的Key,注意：现阶段请随意填入一个不为空的字串。

**AndroidManifest.xml 示例**

    <?xml version="1.0" encoding="utf-8"?>
	<manifest package="com.ynyx.epic.xpush.example"
          xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Required -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- optional -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <!-- Required AppKey-->
        <meta-data android:value="你的应用的APPkey" android:name="XPUSH_APPKEY"></meta-data>
        
        <activity android:name=".activity.MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
		<!-- Required -->
        <receiver
            android:name="com.ynyx.epic.xpush.recevier.PushAlarmReceiver"
            android:enabled="true"
            android:exported="false">
           
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
            
            <intent-filter>
                <action android:name="android.intent.action.USER_PRESENT" />
            </intent-filter>
            
            <intent-filter>
                <action android:name="android.intent.action.PACKAGE_ADDED" />
                <action android:name="android.intent.action.PACKAGE_REMOVED" />
                <data android:scheme="package" />
            </intent-filter>
            
            <intent-filter android:priority="1000">
                <action android:name="android.provider.Telephony.SMS_RECEIVED" />
            </intent-filter>
            
            <intent-filter android:priority="1000">
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
        </receiver>
		<!-- Required -->
        <service android:name="你自定义的PushServices">
            <intent-filter>
                <action android:name="com.ynyx.epic.xpush.service.PushService" />
            </intent-filter>
        </service>
            
    </application>

	</manifest>

### 添加代码 ###
XPush SDK 提供的 API 接口，都主要集中在 com.ynyx.epic.xpush.smack.XPushInterface 类里。

**基础API**

* setDebugMode 设置调试模式

> 

	// You can enable debug mode in developing state. You should close debug mode when release.
	public static void setDebugMode(boolean debugEnalbed)

* init 初始化SDK
> 
 
    public static void init(Context context)

* 自定义PushService，覆盖未实现的方法，配置推送服务相关信息
>

    public class MyPushServices extends PushService {

	    @Override
	    public void onCreate() {
	        super.onCreate();
			//添加消息过滤和处理
	        addPushMessageListener(new TestPushMessageListener(this),
	                new TestPushMessageFilter());
	    }
		//设置用户名
	    @Override
	    protected String getLocalUserName() {
	        return ConstDefine.username;
	    }
		//设置用户密码
	    @Override
	    protected String getLocalUserPwd() {
	        return ConstDefine.userpwd;
	    }
		//设置用户昵称
	    @Override
	    protected String getLocalUserNick() {
	        return ConstDefine.usernickname;
	    }
		//用户的邮箱
	    @Override
	    protected String getLocalUserEmail() {
	        return ConstDefine.userEmail;
	    }
	
		//手动退出时修改该值 返回true
	    @Override
	    protected boolean isLogout() {
	        return false;
	    }
		//设置推送服务的地址
	    @Override
	    protected String getPushServerHost() {
	        return ConstDefine.PUSH_SERVER_HOST;
	    }
		//设置推送服务的端口 默认5222
	    @Override
	    protected int getPushServerPort() {
	        return ConstDefine.PUSH_SERVER_PORT;
	    }
	
	  //设置推送服务的域名 默认为skysea.com
	    @Override
	    protected String getPushServiceName() {
	        return ConstDefine.PUSH_SERVER_NAME;
	    }
	//设置推送用户的资源名称 ，默认为android 设备的id
	    @Override
	    protected String getResourceTag() {
	        return DevicesUtils.getAndroidDeviceId(this);
	    }
    
	}
* 自定义PushMessageFilter实现对推送的信息过滤
>

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

* 和PushMessageListener实现推送的信息的处理，覆盖未实现的方法，获取推送的信息。

>

    public class TestPushMessageListener implements PushMessageListener {

	    private Context mContext;
	    
	    public TestPushMessageListener(Context _context){
	        this.mContext = _context;
	    }
	    
	    //处理在线消息
	    @Override
	    public void processOnlineMessage(Msg message) {
	        //发送一个通知 或是存储到数据库
	        String body = message.getBody();//推送内容
	        NotifyUtils.showNotification(mContext,"在线消息",body);
	    }
	    //处理连线消息
	    @Override
	    public void processOfflineMessage(List<Msg> messages) {
	        //发送一个通知  或是存储到数据库
	        StringBuilder builder = new StringBuilder();
	        builder.append("接收到了 ").append(messages.size()).append(" 条消息");
	        NotifyUtils.showNotification(mContext,"离线消息",builder.toString());
	        
	    }
	}


**入口 调用示例代码（参考 example 项目）**

* init 只需要在应用程序启动时调用一次该 API 即可。调用时机为用户成功登录之后。
* 以下代码请参考 example 项目
>

    public class MainActivity extends Activity {
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	
	        XPushInterface.setDebugMode(false);// debug 
	        XPushInterface.init(this);
	        
	    }
	}


**测试确认**

* 确认所需的权限都已经添加。如果必须的权限未添加，日志会提示错误。
* 确认 AppKey（在移动应用管理平台上生成的）已经正确的写入 Androidmanifest.xml 。
* 确认在程序启动时候调用了init(context) 接口
* 确认测试手机（或者模拟器）已成功连入网络 ＋ 客户端调用 init 后不久，如果一切正常，应有登录成功的日志信息

**推送服务启动失败常见原因**

* 检查 metadata 的 appKey  ，如果不存在、无效，则启动失败
* 检查 Androidmanifest.xml，如果有 Required 的权限不存在，则启动失败
* 连接服务器登录，如果存在网络问题，则登陆失败,或者前面两步有问题，则启动失败

