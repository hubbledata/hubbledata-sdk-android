# Android SDK 使用文档 #

## 权限 ##

需要许可如下权限（添加到 `AndroidManifest.xml` 文件中）

    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.INTERNET"/>

若App中开启了定位权限（Android 6.0及以上需动态授权），sdk将自动获取定位数据

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

## 导入 SDK ##

将下载包里面的 mobidroid.jar 放入 App 项目 libs 目录中

## 启用 ##

### Android 4.0 及以上版本（推荐）

自定义`Application`，并在`onCreate()`方法中添加如下代码，开启统计功能

	DATracker.enableTracker(getApplicationContext(), "demo", "1.0", "Google Play", true, false);

参数依次为，当前 Context 实例、程序的 app key、版本和来源渠道、是否允许 SDK 在会话开始和进入后台时自动上传数据、是否只在 wifi 环境下发送数据

**设置为只在 WIFI 下发送数据，会导致服务器接收数据延迟，对统计系统结果的及时性会产生影响，不建议使用**

**App Key 可从移动分析系统网站获取，不得使用为空值或者 null, 多次调用参数请确保一致**

例如，定义`MyApplication`继承`Application`

	public class MyApplication extends Application {
	
	    @Override
	    public void onCreate() {
	        super.onCreate();
	
	        DATracker.enableTracker(getApplicationContext(), "demo", "1.0", "Google Play", true, false);
	    }
	}
	
并在`AndroidManifest.xml`注册

	<application
	    android:name=".MyApplication"
	    ...>
	    ...
	</application>

### Android 4.0 以下版本

在 App 中所有的 Activity `onCreate` 方法中调用如下代码，开启统计功能

参数依次为，当前 Activity 实例、程序的 app key、版本和来源渠道

    DATracker.enableTracker(this, "demo", "1.0", "Google Play");

如需要禁用 SDK 自动上传数据功能，调用

    DATracker.enableTracker(this, "demo", "1.0", "Google Play", false);

如需要设置只在 wifi 环境下发送数据，调用

    DATracker.enableTracker(this, "demo", "1.0", "Google Play", true, true);

Android 4.0以下版本必须在 App 中所有的 Activity `onResume` 方法中调用如下方法，标识用户会话开始。Android 4.0及以上版本无需调用。

    DATracker.getInstance().resume();

Android 4.0以下版本必须在 App 中所有 Activity `onPause` 方法添加调用，标识用户会话结束。Android 4.0及以上版本无需调用。

    DATracker.getInstance().close();

**如果应用中包含 Activitiy 类继承于自定义 Activity 衍生类，则只需在该类加入上述代码即可，即 `onResume` 中添加 `resume`，`onPause` 中添加 `close` 方法，如果 Main Activity 也继承于该类，则还需在 `onCreate` 中添加 `enableTracker`，其子类不需要添加。但其他非集成于此类的 Activitiy 类必须按上述规则添加代码。**

## 支持Debug模式（默认关闭）##

开启Debug模式。建议在自定义`Application`的 onCreate 中调用。
	
	DATracker.getInstance().setDebugMode(true);
	
关闭Debug模式。建议在自定义`Application`的 onCreate 中调用。
	
	DATracker.getInstance().setDebugMode(false);

## 是否读取定位

当app具备定位权限且该接口设置为true时，读取定位数据。否则，不读取。默认为false。

	DATracker.getInstance().enableLocationAccess(true);

## 手动发送数据

手动发送数据
	
	DATracker.getInstance().upload();
	
## 手动禁用自动上传

这里的自动上传仅仅指的是允许 SDK 在会话开始和进入后台时自动上传数据

	DATracker.getInstance().setAutoUploadOn(false);
    
## 设置两次数据发送的最小时间间隔

当用户调用该接口设置两次数据发送的最小时间间隔interval(ms)时，开启固定间隔自动上传数据，默认关闭。当interval < 15000ms 时，仍按照默认值 15000ms 计算，即用户设置的间隔时间不得低于默认值 15000ms。
	
	public void setFlushInterval(long flushInterval);
	
## 设置本地数据库缓存的最大记录数

当用户调用该接口设置本地数据库缓存的最大记录数时，开启超出阈值立即上传数据，默认关闭。当用户设置的 flushBulkSize < 100 时，仍按照默认值 100 计算，即用户设置的最大记录数不得低于默认值 100。
	
	public void setFlushBulkSize(int flushBulkSize);

## 手动开启只在 WIFI 下发送数据

    DATracker.getInstance().setSendOnWifiOn(true);
    
**设置为只在 WIFI 下发送数据，会导致服务器接收数据延迟，对统计系统结果的及时性会产生影响，不建议使用**

## 推广跟踪 ##

启用推广追踪，请在 Main Activity 里紧跟 `启用 API` 后调用

    DATracker.getInstance().enableCampaign();

**此方法只需要在 自定义Application 或 Main Activity 内调用，旧版接口，不推荐使用**

## 获取 Device ID ##

    [[DATracker sharedTracker] getDeviceId];

**该 Device ID 并非 Apple UDID, 仅用户系统本身设备去重用途, 并且可能根据 Apple 政策做相应调整, 不保证长期唯一性**

## 用户帐号管理 ##

在用户登录后，请调用如下接口，参数为用户帐号

    public void loginUser(String userId);

当用户登出后，请调用

    public void logoutUser();

**如登录发生在需要捕捉事件后，则该事件无用户信息**

## 用户位置记录 ##

在拿到用户经纬度时, 调用如下接口记录用户位置

    public void setLocation(double latitude, double longitude);

## 事件捕捉 ##

调用如下方法进行事件捕捉

    public void trackEvent(String eventId);
    public void trackEvent(final String eventId, final Map<String, String> attributes);

eventId 为事件标识，如 "login", "buy"

    DATracker.getInstance().trackEvent("login");

attributes 为自定义字段名称，格式如 "{"money":"100", "timestamp":"1357872572"}"

可对事件发生时的其他信息进行记录

    Map<String, String> attr = new HashMap<String, String>();
    attr.put("money", "100");
    DATracker.getInstance().trackEvent("login", attr);

如果需要记录事件发生持续时间，可调用如下接口

    public void trackEvent(String eventId, int costTime);
    public void trackEvent(final String eventId, final int costTime, final Map<String, String> attributes);

如果需要记录事件发生时的位置信息, 可调用如下接口

    public void trackEvent(final String eventId, double latitude, double longitude);
    public void trackEvent(final String eventId, double latitude, double longitude,
                           final Map<String, String> attributes)
                           
如果需要记录发生持续时间以及记录事件发生时的位置信息, 可调用如下接口
	
	public void trackEvent(final String eventId, final int costTime, double latitude, double longitude,
	                           final Map<String, String> attributes)

**虽然在任何地方都可以进行事件捕捉，但最好不要在较多循环内或者非主线程中调用，以及最好不要使用很长 eventID 或者 key value 值，否则会增加 SDK 发送的数据量**

## 事件自定义通用属性 ##

特别地，如果某个事件的属性，在所有事件中都会出现，可以将该属性设置为事件通用属性，通用属性会保存在 App 本地，可调用如下接口：

	public void registerSuperProperties(Map<String, String> superProperties);

成功设置事件通用属性后，再通过 trackEvent 追踪事件时，事件通用属性会被添加进每个事件中。重复调用 registerSuperProperties 会覆盖之前已设置的通用属性。

如果不覆盖之前已经设定的通用属性，可调用：

	public void registerSuperPropertiesOnce(Map<String, String> superProperties);

查看当前已设置的通用属性，调用：

	public Map<String,String> currentSuperProperties();

删除一个通用属性，调用：

	public void unregisterSuperProperty(String superPropertyName);

删除所有已设置的事件通用属性，调用：

	public void clearSuperProperties();

**当事件通用属性和事件属性的 Key 冲突时，事件属性优先级最高，它会覆盖事件公共属性。**

## 事件耗时统计 ##

可以通过计时器统计事件的持续时间，默认的时间单位是毫秒。首先，在事件开始时调用 trackTimer 记录事件开始时间，该方法并不会真正发送事件，接口为

	public void trackTimer(final String eventId);
	public void trackTimer(final String eventId, final TimeUnit timeUnit);

调用trackEvent 时，若已记录该事件的开始时间，SDK会在追踪相关事件时自动将事件持续时间记录在事件属性中，并删除该事件定时器。

清除所有的事件定时器，调用：

	public void clearTimedEvents();

多次调用 trackTimer 时，相同 eventId 的事件的开始时间以最后一次调用时为准。


## 异常捕捉 ##

在 try catch block 里面调用，参数为 Exception (含子类)实例

    try {
    	int b = 1/0;
    } catch (ArithmeticException e) {
    	DATracker.getInstance().trackException(e);
    }

如还需要记录 Callstack，可调用

    DATracker.getInstance().trackExceptionWithCallstack(e);

如需要 crash 捕捉, 需要在启用 SDK 后在每个需要捕捉 crash 信息的线程中调用如下方法来开启该功能

	Thread.setDefaultUncaughtExceptionHandler(new DATracker.UncaughtExceptionHandler());

如需要在 Activity 中捕捉 crash，则需要在 onCreate 里面调用如下方法

    Thread.setDefaultUncaughtExceptionHandler(new DATracker.UncaughtExceptionHandler());

如需捕捉应用运行过程中的所有 crash，则需在自定义`Application`的 onCreate 中调用如下方法。推荐使用。

    Thread.setDefaultUncaughtExceptionHandler(new DATracker.UncaughtExceptionHandler());


## 屏幕 View 捕捉 ##

screenName 为当前 View 名称

    public void trackScreen(String screenName);

## 搜索动作捕捉 ##

keyword 为搜索关键词，searchType 为搜索类型，比如新闻搜索，视频搜索等

    public void trackSearch(String keyword, String searchType);

## 分享动作捕捉 ##

content 为分享内容，from 为分享发生地，to 为分享目的地，比如新浪微博，网易微博等

    public void trackShare(String content, String from, String to);

## 用户任务记录 ##

对用户的任务进行记录，参数为任务 id 和任务失败原因，可用于用户行为完成，用户行为耗时等统计。

    DATracker.getInstance().trackOnMissionBegan("mission-1")
    DATracker.getInstance().trackOnMissionAccomplished("mission-1");
    DATracker.getInstance().trackOnMissionFailed("mission-2", "no power");
    

## 设置用户属性 ##

为了更准确地提供针对人群的分析服务，可以使用 SDK 的 DATracker$People 设置用户属性。用户可以在留存分析、分布分析等功能中，使用用户属性作为过滤条件，精确分析特定人群的指标。

获取当前用户信息设置实例

	DATracker.getInstance().getPeople();

设置当前用户信息属性（全局或单个）

	public void set(Map<String, String> properties);
	public void set(String property, String value);

设置当前用户信息属性（如果该属性已经设置过，则忽略）。与 set() 方法不同的是，如果被设置的用户属性已存在，则这条记录会被忽略而不会覆盖已有数据，如果属性不存在则会自动创建。因此，setOnce() 比较适用于为用户设置首次激活时间、首次注册时间等属性。

	public void setOnce(Map<String, String> properties);
	public void setOnce(String property, String value);

清除当前用户信息属性

	public void unset(String property);

删除当前用户记录

	public void deleteUser();

记录用户消费接口（用于用户价值评估功能）。正数金额代表客户的消费金额，负金额代表客户的退款。

	public void trackCharge(double amount);
	public void trackCharge(double amount, Map<String, String> properties);
	
清除用户消费记录

	public void clearCharges();

## 设置用户公共属性 ##

设置用户账户

    public People setAccount(String account);

设置用户真实姓名

	public People setRealName(String realName);

设置用户性别（0-女，1-男，2-未知）

    public People setGender(int gender);

设置用户出生日期（SDK会自动转换格式：yyyy-MM-dd ）

    public People setBirthday(Date birthday);
    
连续设置用户基本信息

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    try {
        Date date = dateFormat.parse("1989-08-03");
        DATracker.getInstance().getPeople().setAccount("nailperry").setRealName("张三").setGender(1).setBirthday(date);
    } catch (ParseException e) {
        e.printStackTrace();
    }
    
一次性设置用户基本信息

    public void setPopulationAttributes(String account, String realName, Date birthday, int gender);

设置用户地址信息

    public void setLocation(String country, String region, String city);

## 页面统计（2.1新增）

### 版本要求

	Android 4.0+

### 默认采集页面数据

`EventID` 为 固定统一的 `da_screen`

`dateType` 为 `pv`

`properties` 的参数设计如下：

(1) `$screenName` 为当前页面的名称；

- 当页面统计的当前页面为`Activity`时，`$screenName`为`ActivityName`；
- 当页面统计的当前页面为`Activity`下的`Fragment`时，`$screenName`为`ActivityName/FragmentName`。

(2) `$screenTitle` 为当前页面的`title`，可为空；

### 页面统计开关（默认为true）

开启
    
    DATracker.getInstance().enablePageTrack(true);

关闭 
    
    DATracker.getInstance().enablePageTrack(false);
    
### 自定义页面信息

- Fragment

对于 App 中的核心页面`Fragment`，我们提供了一个接口 `FragmentAutoTracker`：

    public interface FragmentAutoTracker {
        /**
         * 返回当前页面的Url
         * 用作下个页面的referrer
         * @return String
         */
        String getScreenUrl();
    
        /**
         * 返回当前页面的Title
         * @return String
         */
        String getScreenTitle();
    
        /**
         * 返回自定义属性集合
         * 我们内置了两个属性:$screenName,代表当前页面名称；$screenTitle 为当前页面的title，可为空； 
         * 默认情况下，
         * $screenName会采集当前Activity的CanonicalName,即:
         * activity.getClass().getCanonicalName(), 如果想自定义页面名称, 可以在Map里put该key进行覆盖。注意:screenName的前面必须要要加"$"符号。
         * $screenTitle 会通过activity.getTitle().toString()采集页面主题，如果想自定义页面主题，可重写getScreenTitle()函数。
         *
         * @return Map<String, String>
         */
        Map<String, String> getTrackProperties();
    }
    
当用户实现该接口时，SDK 会将 getTrackProperties 返回的属性（Map<String, String>类型）加入页面统计事件的属性中，作为用户访问该页面时的事件属性；SDK 会将 getScreenUrl 返回的字符串作为页面的 Url Schema，记录在页面统计事件的 $url 属性中，并且当用户切换页面时，将前一个页面中的 Url Schema 作为当前页面事件的 $referrer 属性。

例如：

    public class OrderDetailFragment extends Fragment implements FragmentAutoTracker {
        @Override
        public String getScreenUrl() {
            return "da://page/order/detail?orderId=888888";
        }
        
        @Override
        public String getScreenTitle() {
            return "订单详情";
        }
    
        @Override
        public Map<String, String> getTrackProperties() {
            Map<String, String> properties = new HashMap<>();
            properties.put("orderId", "888888");
            properties.put("manufacturer", "da");
            return properties;
        }
    }
    
- Activity

对于 App 中的核心页面`Activity`，我们提供了一个接口 `ActivityAutoTracker`：

	public interface ActivityAutoTracker extends FragmentAutoTracker{
    	boolean trackFragmentAsScreen();
	}

该接口相比于Fragment多了一个`trackFragmentAsScreen()`函数，当该函数返回 true 时，则将Activity中的Fragment作为页面进行统计，而不统计Activity页面。当该函数返回false时，则只统计Activity页面，不统计Activity中的Fragment。此功能需结合Fragment数据采集使用。

> 当对Activity中的Fragment进行数据采集时，请确保`trackFragmentAsScreen()`返回true。

### 过滤部分页面

开启PageTrack后，如果需要指定部分页面不被PageTrack，可以按照下面示例指定哪些页面不被PageTrack：

    HashSet<String> list = new HashSet<>();
    list.add(MainActivity.class.getCanonicalName());
    DATracker.getInstance().filterAutoTrackActivities(list);

### 采集Fragment数据

情形一：

一个`Activity`同一时刻在屏幕中只将一个`Fragment`作为页面进行统计的情形，例如，主页(`Activity`)通过底部`Tab`切换`Fragment`，每切换到一个`Tab`，便将当前`Tab`对应的`Fragment`作为页面进行统计。

对于需要进行页面统计的`Fragment`，除了在`Fragment`的`onResume()`、`onPause()`生命周期中分别加入`onFragmentResume`和`onFragmentPause`代码外，APP开发人员还需要根据`Fragment`的管理方式重写`setUserVisibleHint`或`onHiddenChanged`方法。如果不清楚是否应该重写`setUserVisibleHint`或`onHiddenChanged`方法，建议这两个方法都重写。
	
建议上述代码可以在一个基类`TrackedFragment`中做，让所有需要进行页面统计的`Fragment`类都继承这个基类`TrackedFragment`，就完成了所有子类页面埋点。

代码示例如下：

    public class TrackedFragment extends Fragment{
	    @Override
	    public void onResume() {
	        super.onResume();
	        DATracker.getInstance().onFragmentResume(this);
	    }
	
	    @Override
	    public void onPause() {
	        super.onPause();
	        DATracker.getInstance().onFragmentPause(this);
	    }
	
	    @Override
	    public void setUserVisibleHint(boolean isVisibleToUser) {
	        super.setUserVisibleHint(isVisibleToUser);
	        DATracker.getInstance().setFragmentUserVisibleHint(this, isVisibleToUser);
	    }
	
	    @Override
	    public void onHiddenChanged(boolean hidden) {
	        super.onHiddenChanged(hidden);
	        DATracker.getInstance().onFragmentHiddenChanged(this, hidden);
	    }
	}    

当`Activity`实现`ActivityAutoTracker`接口且`trackFragmentAsScreen()`返回true时，SDK 会自动识别`Activity`中当前显示的`Fragment`并进行数据采集。反之，当`Activity`没有实现`ActivityAutoTracker`接口或者`trackFragmentAsScreen()`返回false时，不采集该`Activity`下的任何Fragment数据。

    
情形二：（局限性）

当一个`Activity`下同时在屏幕中显示多个调用了SDK的`onFragmentResume`、`onFragmentPause`接口的`Fragment`时，我们无法判断将哪个作为页面统计。