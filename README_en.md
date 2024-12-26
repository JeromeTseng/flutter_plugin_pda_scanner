<img src="https://gitee.com/zengxingshun/flutter_plugin_pda_scanner/raw/master/photos/LOGO.png" alt="flutter_plugin_pda_scanner" style="zoom: 67%;" />

<h2 align="center" style="font-weight: bold;margin-top:-20px">flutter_plugin_pda_scanner v3.0.5</h2>

<h4 align="center" style="margin-top: -5px;">A Flutter Plugin for PDA Barcode Scanning</h4>

<p align="center">
	<a href="https://gitee.com/zengxingshun/flutter_plugin_pda_scanner"><img src="https://img.shields.io/badge/pda_scanner-v3.0.5-brightgreen.svg" alt=""></a>
	<a href="https://gitee.com/zengxingshun/flutter_plugin_pda_scanner/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg" alt=""></a>
    <a href=""><img src="https://img.shields.io/badge/WeChat-1340756449-blue.svg" alt=""></a>
    <a href="https://gitee.com/zengxingshun"><img src="https://img.shields.io/badge/author-Jerome-08979c" alt=""></a>
    <a href="https://gitee.com/zengxingshun/flutter_plugin_pda_scanner/blob/master/LICENSE"><img src="https://img.shields.io/badge/language-dart%20/%20kotlin-red.svg" alt=""></a>
</p>




# Introduction  ✨

This is a PDA barcode scanning plugin specially designed for Flutter developers, supporting multiple brands and models of PDA devices, including but not limited to Zebra, Hikvision, Invengo, Speedata, and Seuic. The plugin offers both automatic and manual initialization methods to accommodate different development needs and scenarios.

### 🔹Features：

- **Multi-device support**: Compatible with multiple mainstream PDA devices after testing.
- **Broadcast listening**: Supports manual registration of broadcast listeners for PDA barcode scanning via broadcast behavior.
- **Event listening**: Can listen to multiple scanning events at the same time, distinguished by tags.
- **Rich API**: Provides a variety of APIs, including initialization, listening, and closing the scanner.
- **Sound prompts**: Includes functions for successful and failed sound prompts.

### 🔹Usage ：

1. **Installation**: Add dependency through `pubspec.yaml`.
2. **Import**: Import the `flutter_plugin_pda_scanner` package in your Flutter project.
3. **Initialization**: Initialize the plugin in the `main` function and use `await` to wait for completion.
4. **Event listening**: Set up event listening with the `on` method, using tags as identifiers.
5. **Cancel listening**: Use the `off` method to cancel listening for a specific tag or `offAll` to cancel all listening.

### Supported Models：

```text
Zebra: MC3300x
Hikvision: DS-MDT201
Invengo: Not specified
Speedata: T60
Seuic: CRUISE2 5G
...
Also supports manual registration of broadcast listeners for PDA barcode scanning based on broadcast behavior.
```

**You can  [download](https://gitee.com/zengxingshun/flutter_plugin_pda_scanner/releases/download/V3.0.5/pda_scanner-V3.0.5.apk)  the sample app for testing first. If you need to integrate native scanning of PDAs, please add me on WeChat for feedback, and welcome to fork and contribute._**

| <img src="https://gitee.com/zengxingshun/flutter_plugin_pda_scanner/raw/master/photos/home.jpg" style="zoom: 67%;" /> | <img src="https://gitee.com/zengxingshun/flutter_plugin_pda_scanner/raw/master/photos/model_page.jpg" style="zoom: 67%;" /> | <img src="https://gitee.com/zengxingshun/flutter_plugin_pda_scanner/raw/master/photos/set_broadcast.jpg" style="zoom: 67%;" /> |
|:--------------------------------------------------:|:--------------------------------------------------------:|:-----------------------------------------------------------:|

➡ **For integrating Bluetooth scanning guns/USB scanning guns, please refer to another excellent open-source library: [liyufengrex/flutter_scan_gun: flutter：usb 即插款扫码枪通用方案。](https://github.com/liyufengrex/flutter_scan_gun).**

# Installation 📔

## 1. Installation

Add the following to your `pubspec.yaml` file

```yaml
dependencies:
  flutter_plugin_pda_scanner: ^3.0.5
```

## 2. Import

```dart
import 'package:flutter_plugin_pda_scanner/pda_utils.dart';
```

## 3. Initialization

* ### Automatic Initialization

```dart
// Initialize before the runApp call in the main function
void main() async {
  await PdaUtils.init();
  runApp(const MyApp());
}

// Listen for events, can listen to multiple events distinguished by tags
PdaUtils.on("tag", (barcode) {
// Receive the callback barcode...
});

// Cancel listening on the tag
PdaUtils.off("tag");
```

* ### Manual Initialization

```dart
// Pass in the broadcast action and the data label to be obtained
await PdaUtils.initByCustom("com.action.scannersrvice...","data");

// Listen for events, can listen to multiple events distinguished by tags
PdaUtils.on("tag", (barcode) {
// Receive the callback barcode...
});

// Cancel listening on the tag
PdaUtils.off("tag");
```

🔷 **_The tag here is equivalent to an ID, each interface defines a unique identifier, equivalent to marking which interface is listening for the scanning event, and when the page is destroyed, it also cancels listening based on this ID to avoid memory leaks._**

🔷 **_The bar code content will be processed, and the first and last whitespace characters (newlines, tabs, Spaces) will be replaced with empty strings, but the whitespace in the middle of the characters will not be replaced._**

## 4. Api Details

| API                    | **Description**                                              | **Example**                                |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------ |
| init                   | <div style="width:220px">Initialize the PDA plugin, call before the runApp method, note: There are many asynchronous operations inside this method, please combine with await to wait for the init operation to complete.</div> | await PdaUtils.init();                     |
| initByCustom           | <div style="width:220px">Manually initialize the PDA plugin<br>action：Broadcast behavior<br>label：Barcode content acquisition label</div> | await PdaUtils.initByCustom(action,label); |
| getInitLogList         | Get initialization logs                                      | PdaUtils.getInitLogList();                 |
| isThisPDASupported     | Whether this PDA device supports barcode scanning            | PdaUtils.isThisPDASupported();             |
| getPDAModel            | Get the device model name                                    | PdaUtils.getPDAModel();                    |
| getPlatformVersion     | Get the Android system version                               | PdaUtils.getPlatformVersion();             |
| on                     | <div style="width:220px">Listen for barcode scanning events, each scanning event passes in a tag string as an independent listening identifier</div> | PdaUtils.on('tag',(barcode){...});         |
| getOnTagList           | Get the list of subscribed tag identifiers                   | PdaUtils.getOnTagList();                   |
| off                    | Cancel listening on the tag                                  | PdaUtils.off('tag');                       |
| offAll                 | Cancel all listening events                                  | PdaUtils.offAll();                         |
| errorSoundDudu         | Dudu warning sound                                           | PdaUtils.errorSoundDudu();                 |
| successSoundHumanVoice | Human voice prompt for successful scanning                   | PdaUtils.successSoundHumanVoice();         |
| errorSoundHumanVoice   | <div style="width:220px">Human voice prompt for failed scanning, can pass in a bool type parameter playErrorSoundDudu, which is whether to play the dudu warning sound when playing the failed human voice, this parameter defaults to true</div> | PdaUtils.errorSoundHumanVoice();           |
| navigateToSystemHome   | <div style="width:220px">Return to the system home screen, natively returning to the system home screen and then entering the app will restart the app, intercepting the call to this method will not have this situation</div> | PdaUtils.navigateToSystemHome();           |
| closeScanner           | Manually close the scanner                                   | PdaUtils.closeScanner();                   |

## 5. PDA Scanning Example

```dart
import 'package:flutter/material.dart';
import 'package:flutter_plugin_pda_scanner/pda_utils.dart';

void main() async {
  // Initialize PDA scanning, add the await keyword to wait for 
  await PdaUtils.init();
  runApp(MaterialApp(
    title: 'PDA Scanning Example',
    theme: ThemeData(
      colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      useMaterial3: true,
    ),
    home: const MyHomePage(title: 'PDA Scanning Example'),
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
        backgroundColor: Theme
            .of(context)
            .colorScheme
            .inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: () {
                // Listen for events, you can listen to multiple events and use tags to distinguish
                PdaUtils.on("tag", (barcode) {
                  showDialogFunction(context, barcode);
                });
              },
              child: const Text("Listen"),
            ),
            ElevatedButton(
              onPressed: () {
                // Cancel listening
                PdaUtils.off("tag");
              },
              child: const Text("Cancel Listen"),
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
          title: const Text("Barcode Content"),
          content: Text(barcode),
        );
      },
    );
    _dialogShow = false;
  }
}
```

# Additional Notes for Zebra PDA ⚡

**For ZEBRA (Zebra) PDA experiencing crashes or other issues, you need to make the following additional configurations. You need to add the following content to your Flutter project's Android directory's <span style="color:red;font-weight:bold">
AndroidManifest.xml</span> . **

### 1、Add under the `manifest` node

```xml
<uses-permission android:name="com.symbol.emdk.permission.EMDK" />
<queries>
	<package android:name="com.symbol.emdk.emdkservice" />
</queries>
```

### 2、Add under the `application` node:

```xml
<uses-library android:name="com.symbol.emdk" android:required="false" />
<uses-library android:name="com.rscja.scanner" android:required="false" />
```

### 3、Create a new libs folder in the project [\android\app]

Put `emdk-11.0.129.jar` into this folder, which can be downloaded from the Zebra official website or from the assets provided.

Add in your `dependencies` tag of `build.gradle` files in your project's `app` directory

```groovy
// ZEBRA PDA
compileOnly files('libs/emdk-11.0.129.jar')
```

Below are some related Zebra issues:

[android - How to use Zebra EMDK in release build? - Stack Overflow](https://stackoverflow.com/questions/70899282/how-to-use-zebra-emdk-in-release-build)

[Zebra EMDK Setup - TechDocs](https://techdocs.zebra.com/emdk-for-android/latest/guide/setup/)

[Basic Scanning with Barcode API - TechDocs (zebra.com)](https://techdocs.zebra.com/emdk-for-android/11-0/tutorial/tutbasicscanningapi/)

# Build Exception Instructions 💥

**If you encounter the following exception when running`flutter build apk`**

```text
ERROR: Missing classes detected while running R8. Please add the missing classes or apply additional keep rules that are generated in [your_flutter_project]\build\app\outputs\mapping\release\missing_rules.txt.
ERROR: R8: Missing class com.symbol.emdk.EMDKBase (referenced from: void io.github.jerometseng.pdascanner.pda_type.zebra.ZebraConfig.onOpened(com.symbol.emdk.EMDKManager))
Missing class com.symbol.emdk.EMDKManager$EMDKListener (referenced from: void io.github.jerometseng.pdascanner.pda_type.zebra.ZebraConfig.open() and 1 other context)
Missing class com.symbol.emdk.EMDKManager$FEATURE_TYPE (referenced from: void io.github.jerometseng.pdascanner.pda_type.zebra.ZebraConfig.close() and 2 other contexts)
Missing class com.symbol.emdk.EMDKManager (referenced from: com.symbol.emdk.EMDKManager io.github.jerometseng.pdascanner.pda_type.zebra.ZebraConfig.emdkManager and 4 other contexts)
Missing class com.symbol.emdk.EMDKResults$STATUS_CODE (referenced from: void io.github.jerometseng.pdascanner.pda_type.zebra.ZebraConfig.open())
Missing class com.symbol.emdk.EMDKResults (referenced from: void io.github.jerometseng.pdascanner.pda_type.zebra.ZebraConfig.open())
......

FAILURE: Build failed with an exception.
```

**Please add the following content to the` [your_flutter_project]\android\app\proguard-rules.pro  ` file**

```
-dontwarn com.symbol.emdk.**
```

As shown in the figure：

<img src="https://gitee.com/zengxingshun/flutter_plugin_pda_scanner/raw/master/photos/r8mix.png" style="zoom: 80%;" />
