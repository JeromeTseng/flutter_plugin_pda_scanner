# pda_scanner

* 支持多种PDA扫码的插件

## 开始
您需要在您项目的安卓目录下的`AndroidManifest.xml`中加入以下内容
### 1、在 `manifest` 节点下加入
```
    <uses-permission android:name="com.symbol.emdk.permission.EMDK"/>
```
### 2、在 `application` 节点下加入

```
    <uses-library android:name="com.symbol.emdk"/>
    <uses-library android:name="com.rscja.scanner"/>
```

