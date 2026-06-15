## iot-android-sdk-demo

> 📖 中文文档请参阅 [readme_zh.md](./readme_zh.md)

---

### 1. Overview

This demo demonstrates how to build an IoT application from scratch using **QuecIoTAppSDK**. The SDK is organized into several functional modules, giving developers a clear understanding of how different features are implemented, including:

- User registration flow
- Device binding and control
- Device group management

Supported device types: **Cellular devices** and **Wi-Fi / Bluetooth devices**.  
Device control is supported over both **HTTP** and **WebSocket**.

---

### 2. API / Properties

### Initialization

```java
// Call this inside your Application's onCreate() method when the app starts.
QuecSDKMergeManager.getInstance().init(this);
```

### Configure User Domain, Domain Secret & Cloud Service Type

```java
public void initProject(int serviceType, String userDomain, String domainSecret)

// Example — call inside Application onCreate():
QuecSDKMergeManager.getInstance().initProject(0, userDomain, domainSecret);
```

| Parameter      | Required | Description                                                       |
| -------------- | -------- | ----------------------------------------------------------------- |
| `serviceType`  | Yes      | `0` — Domestic (China); non-`0` — International                  |
| `userDomain`   | Yes      | User domain, generated when creating an App on the DMP platform   |
| `domainSecret` | Yes      | User domain secret, generated when creating an App on the DMP platform |
