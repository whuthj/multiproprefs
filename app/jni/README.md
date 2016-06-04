# VideoPlayer
##依赖
ffmpeg-2.5+, SDL2.0.3+, android 2.3.3+(sdk>=10).

##配置
./bootstrap.sh

##编译:
ndk-build -j$(nproc) 2>&1 | tee build.log