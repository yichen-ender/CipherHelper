#!/bin/bash
set -e
PROJECT=/data/user/work/new_src
PLATFORM_JAR=/opt/android-sdk/platforms/android-34/android.jar
BUILD_TOOLS=/opt/android-sdk/build-tools/34.0.0

# 创建密钥库（如果不存在）
if [ ! -f $PROJECT/debug.keystore ]; then
  keytool -genkey -v -keystore $PROJECT/debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -validity 10000 -dname "CN=Debug,O=Android,C=US"
fi

# 编译资源
cd $PROJECT
$BUILD_TOOLS/aapt2 compile --dir res -o res_compiled.zip
$BUILD_TOOLS/aapt2 link -o bin/unsigned.apk --java gen --manifest AndroidManifest.xml -I $PLATFORM_JAR --auto-add-overlay res_compiled.zip

# 编译Java源码
javac -source 1.8 -target 1.8 -bootclasspath $PLATFORM_JAR -d bin gen/com/hexihe/cipherhelper/R.java src/com/hexihe/cipherhelper/*.java

# 转换为DEX
mkdir -p dex
$BUILD_TOOLS/d8 --dex --output=dex/classes.dex bin/

# 打包APK
cp bin/unsigned.apk bin/base.apk
zip -j bin/base.apk dex/classes.dex

# 对齐
$BUILD_TOOLS/zipalign -p -f 4 bin/base.apk bin/aligned.apk

# 签名
$BUILD_TOOLS/apksigner sign --ks debug.keystore --ks-pass pass:android --key-pass pass:android --out /workspace/密码助手_2.1.apk bin/aligned.apk

# 验证
$BUILD_TOOLS/apksigner verify /workspace/密码助手_2.1.apk
echo 'BUILD SUCCESS'
