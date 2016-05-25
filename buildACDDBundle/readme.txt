<------------------------------------>
<---------ACDD bundle构建说明--------->
<------------------------------------>
1. 将插件编译出来的 apk 放入 apks 目录内, 在放入前确认这个目录中没有其它 apk 文件
2. 运行相应的脚本. windows 运行 acdd.bat, unix 运行 acdd.sh, 输出结果示例如下时可执行下一步 :

    rename: CMScreenSaverSDK-release.apk -> libcom_cms_plugin_screensaver.so
    ApkPreProcess.preProcess() processed libcom_cms_plugin_screensaver.so
    =====wtrited  cache hash to md5i/628cb41e80a0cd1ce67dc76f47d41b08
    [{"pkgName":"com.cms.plugin.screensaver","version":"1.0","activities":[...

3. 将 json 目录内生成的 bundle-info.json 文件复制到插件对应的协调模块的 assets 目录下, 例如 : 市场插件放在 MarketCoordinator/src/main/assets 内.
