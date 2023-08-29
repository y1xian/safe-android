# Android安全工具类

### 模块彼此之间没有依赖，只统一引入轻量级的(security-common)

minSdk:21
targetSdk:33
jdk:11

- 下沉的通用工具库(security-common)
- 基础模块(security-base)
- 应用组件安全(security-app)：四大组件安全，可防止应用crash
- 加解密算法(security-encrypt)

```groovy
    allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.y1xian.safe-android:security-base:latest_version'
    implementation 'com.github.y1xian.safe-android:security-encrypt:latest_version'
    implementation 'com.github.y1xian.safe-android:security-app:latest_version'
}
```