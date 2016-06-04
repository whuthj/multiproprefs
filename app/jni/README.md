# VideoPlayer
##依赖
ffmpeg-2.5+, SDL2.0.3+, android 2.3.3+(sdk>=10).

##配置
1.解压ffmpeg-2.8.7.tar.bz2到当前目录命名为ffmpeg
2.解压SDL2-2.0.4.zip到当前目录命名为SDL(注意大写)
3.执行./bootstrap.sh

##编译:
ndk-build -j$(nproc) 2>&1 | tee build.log