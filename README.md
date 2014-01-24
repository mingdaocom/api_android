##明道Android 版本的SDK，包含了移动应用SSO授权


***

##1、SSO授权功能介绍

使用明道登录的第三方Android、iOS应用可通过明道官方客户端快速完成OAuth2.0授权登录。
##2、SSO授权优势

不需要重复输入明道用户名、密码，只需要一步操作，直接点击授权按钮即可完成授权，增强了操作简便性及帐号安全性。

目前支持SSO的客户端版本（SDK会进行版本识别并以WebView方式向下兼容）

1、Android版明道客户端2.0.5及以上
##3、接入流程
1、下载并导入明道官方SDK。

Android平台：https://github.com/meihua-info/api_android

iOS平台：https://github.com/meihua-info/api_ios

2、参照SDK中所提供的Sample，仔细阅读SDK说明文档。

3、参照说明文档，修改应用信息（包括Appkey、App Secret、Redirect Url），并完成第三方应用中的一些开发工作。

注明：第三方也可根据自身需要，对SDK进行二次开发。


##4、创建应用
公有云请到明道开放平台创建应用 <http://open.mingdao.com> 私有部署请联系管理员创建应用并获取令牌


##5、Android平台接入说明文档

1、此源码是已经嵌入了MDAndroid_SDK的Demo，开发者可以根据此Demo将SDK嵌入到自己的项目中。基于Android SDK 17，工程项目可用Eclipse导入打开即可，其它Android SDK版本请修改project.properties中的target即可。

2、请修改MainActivity中的app_key、app_secret和redirect_uri内容

3、具体流程

![image](https://github.com/meihua-info/api_android/blob/master/doc/1.png)

***