ln -f makefiles/Android_configure.mk jni/ffmpeg/
ln -f makefiles/Android_.mk jni/ffmpeg/
ln -f makefiles/Android.mk jni/ffmpeg/

#echo 'include $(call all-subdir-makefiles)' > jni/Android.mk
#LIBS="avcodec avdevice  avfilter  avformat  avresample  avutil  swresample  swscale"

#echo "APP_MODULES := $LIBS" > jni/Application.mk
#echo "APP_MODULES += SDL2 main" >> jni/Application.mk
#echo "APP_ABI := armeabi-v7a " >> jni/Application.mk
#echo "APP_PLATFORM := android-9" >> jni/Application.mk
