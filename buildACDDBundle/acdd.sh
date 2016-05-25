#!/bin/sh

rm -fr json/*
rm -fr md5i/*
java -jar ACDDExt.jar apks json/bundle-info.json md5i
