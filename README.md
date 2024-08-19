# pda_scanner

* 支持多种PDA扫码的插件

  已测试的型号有：

  ```text
  斑马：MC3300x
  海康威视(Hikivision)：DS-MDT201
  远望谷：不明确
  思必拓：T60
  东集seuic：CRUISE2 5G
  ...
  同时支持根据广播行为手动注册广播监听PDA扫码
  ```
## 安装

将以下内容添加到你的pubspec.yaml文件中

```yaml
dependencies:
  pda_scanner:
   git:
    url: https://gitee.com/zengxingshun/flutter_plugin_pda_scanner.git
```

## 导入

```dart
import 'package:pda_scanner/pda_utils.dart';
```

## 一、使用PDA进行扫码

```dart
// 请在main函数的runApp调用之前初始化
void main() async {
  await PdaUtils.init();
  runApp(const MyApp());
}
    
// 监听事件 可以监听多个事件 用tag进行区分
PdaUtils.on("tag", (barcode) {
  // 接收回调的条码...
});

// 取消tag上的监听
PdaUtils.off("tag");
```
## 二、手动初始化

```dart
// 传入广播action 和要获取的数据标签 label
await PdaUtils.initByCustom("com.action.scannersrvice....","data");

// 监听事件 可以监听多个事件 用tag进行区分
PdaUtils.on("tag", (barcode) {
  // 接收回调的条码...
});

// 取消tag上的监听
PdaUtils.off("tag");
```



### Api详情

api | 说明 | 调用示例
----- | ----- | -----
init|<div style="width:220px">初始化PDA插件，在runApp方法之前调用，注意：该方法内有大量异步操作，请结合await等待init操作完成。</div>|await PdaUtils.init();
initByCustom|<div style="width:220px">手动初始化PDA插件<br>action：广播行为<br>label：扫码内容获取标签</div>|await PdaUtils.initByCustom(action,label);
getInitLogList|获取初始化日志 |PdaUtils.getInitLogList();
isThisPDASupported|该PDA设备是否支持扫码 |PdaUtils.isThisPDASupported();
getPDAModel|获取设备型号名称|PdaUtils.getPDAModel();
getPlatformVersion|获取安卓系统版本|PdaUtils.getPlatformVersion();
on|<div style="width:220px">监听扫码事件，每次扫码事件传入tag字符串作为独立监听标识</div>|PdaUtils.on('tag',(barcode){...});
getOnTagList|获取订阅的tag标识列表|PdaUtils.getOnTagList();
off|取消对tag上的监听|PdaUtils.off('tag');
offAll|取消所有监听事件|PdaUtils.offAll();
errorSoundDudu|嘟嘟警告提示音|PdaUtils.errorSoundDudu();
successSoundHumanVoice|扫码成功的人声提示|PdaUtils.successSoundHumanVoice();
errorSoundHumanVoice|<div style="width:220px">扫码失败的人声提示，可传入bool类型参数playErrorSoundDudu，即播放失败人声时是否播放嘟嘟警告提示音，该参数默认为true</div>|PdaUtils.errorSoundHumanVoice();
navigateToSystemHome|<div style="width:220px">返回系统桌面，原生返回系统桌面后再进入app时会重启app，返回系统桌面时拦截调用该方法后不会有该情况</div>|PdaUtils.navigateToSystemHome();
closeScanner|手动关闭扫码器|PdaUtils.closeScanner();

### PDA扫码示例

```dart
import 'package:flutter/material.dart';
import 'package:pda_scanner/pda_utils.dart';

void main() async {
  // 初始化PDA扫码 加上await关键字等待初始化完成
  await PdaUtils.init();
  runApp(MaterialApp(
    title: 'PDA扫码示例',
    theme: ThemeData(
      colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      useMaterial3: true,
    ),
    home: const MyHomePage(title: 'PDA扫码示例'),
  ));
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  bool _dialogShow = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: () {
                // 监听事件 可以监听多个事件 用tag进行区分
                PdaUtils.on("tag", (barcode) {
                  showDialogFunction(context, barcode);
                });
              },
              child: const Text("监听扫码事件"),
            ),
            ElevatedButton(
              onPressed: () {
                // 取消监听
                PdaUtils.off("tag");
              },
              child: const Text("取消监听扫码事件"),
            )
          ],
        ),
      ),
    );
  }

  /// showDialog
  showDialogFunction(BuildContext context, String barcode) async {
    if (_dialogShow) {
      _dialogShow = false;
      Navigator.of(context).pop();
    }
    _dialogShow = true;
    await showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text("条码内容"),
          content: Text(barcode),
        );
      },
    );
    _dialogShow = false;
  }
}
```

# 说明：

如果项目启动报如下错误

<span style="color:red">Suggestion: add 'tools:replace="android:label"' to \<application> element at AndroidManifest.xml:</span>

`解决方式`

在manifest标签加上 `xmlns:tools="http://schemas.android.com/tools"`

在application标签加上 `tools:replace="android:label"`

如图

<img src='/photos/pic_01.png' width="60%">
<img src='/photos/pic_02.png' width="60%">


## 例外：ZEBRA（斑马）的PDA发生闪退或其他情况则需要做以下额外配置。

您需要在您Flutter项目的安卓目录下的 <span style="color:red;font-weight:bold">  AndroidManifest.xml </span>中加入以下内容
### 1、在 `manifest` 节点下加入
```xml
<uses-permission android:name="com.symbol.emdk.permission.EMDK"/>
<queries>
    <package android:name="com.symbol.emdk.emdkservice" />
</queries>
```
### 2、在 `application` 节点下加入

```xml
<uses-library android:name="com.symbol.emdk" android:required="false"/>
<uses-library android:name="com.rscja.scanner" android:required="false"/>
```

### 3、在项目\android\app下新建 libs 文件夹

将  `emdk-11.0.129.jar` 放入该文件夹中，可在zebra官方进行下载  或者在我提供的assets中进行下载。

在项目app下的build.gradle文件的dependencies下添加

```groovy
// 斑马PDA
compileOnly files ('libs/emdk-11.0.129.jar')
```

以下为Zebra相关问题：

[android - How to use Zebra EMDK in release build? - Stack Overflow](https://stackoverflow.com/questions/70899282/how-to-use-zebra-emdk-in-release-build)

[Zebra EMDK Setup - TechDocs](https://techdocs.zebra.com/emdk-for-android/latest/guide/setup/)

[Basic Scanning with Barcode API - TechDocs (zebra.com)](https://techdocs.zebra.com/emdk-for-android/11-0/tutorial/tutbasicscanningapi/)
