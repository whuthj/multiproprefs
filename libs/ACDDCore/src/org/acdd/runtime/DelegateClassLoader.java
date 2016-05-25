/*
 * ACDD Project
 * file DelegateClassLoader.java  is  part of ACCD
 * The MIT License (MIT)  Copyright (c) 2015 Bunny Blue,achellies.
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */
package org.acdd.runtime;

import android.os.Build;

import org.acdd.framework.Framework;

import org.osgi.framework.Bundle;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/***
 * DelegateClassLoader is ClassLoader custom implementation,used for  bundle class load<br>
 * override  findClass
 ***/
public class DelegateClassLoader extends ClassLoader {
    private Object pathList;

    private static final Map<String, Class<?>> sClassNotFoundExchangeMap;
    static {
        sClassNotFoundExchangeMap = new TreeMap<String, Class<?>>();
        sClassNotFoundExchangeMap.put("ks.cm.antivirus.applock.sdkrule.AppLockActiveProvider",
                org.acdd.android.compat.AppLockActiveProvider.class);
        sClassNotFoundExchangeMap.put("ks.cm.antivirus.applock.theme.ThemeContentProvider",
                org.acdd.android.compat.ThemeContentProvider.class);
        sClassNotFoundExchangeMap.put("ks.cm.antivirus.scan.result.timeline.data.CardsContentProvider",
                org.acdd.android.compat.CardsContentProvider.class);
        sClassNotFoundExchangeMap.put("ks.cm.antivirus.applock.theme.db.ApplockThemeContentProvider",
                org.acdd.android.compat.ApplockThemeContentProvider.class);
        sClassNotFoundExchangeMap.put("ks.cm.antivirus.applock.lockertheme.LockerThemeContentProvider",
                org.acdd.android.compat.LockerThemeContentProvider.class);
    }


    public DelegateClassLoader(ClassLoader classLoader) {
        super(classLoader);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                Class classBaseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");
                if (classBaseDexClassLoader.isInstance(classLoader)) {
                    this.pathList = getClassLoaderFeildValue(classBaseDexClassLoader, classLoader, "pathList");
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    private Object getClassLoaderFeildValue(Class<? extends ClassLoader> clazz, ClassLoader classLoader, String feildName)
            throws NoSuchFieldException, IllegalAccessException {
        Field fieldPath = clazz.getDeclaredField(feildName);
        fieldPath.setAccessible(true);
        return fieldPath.get(classLoader);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return super.loadClass(className);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        ClassLoadFromBundle.checkInstallBundleIfNeed(className);
        Class<?> loadFromInstalledBundles = ClassLoadFromBundle.loadFromInstalledBundles(className);
        if (loadFromInstalledBundles != null) {
            return loadFromInstalledBundles;
        }
        Class<?> clazz = sClassNotFoundExchangeMap.get(className);
        if (clazz != null) {
            return clazz;
        }
        throw new ClassNotFoundException("Can't find class " + className + printExceptionInfo() + " " + ClassLoadFromBundle.getClassNotFoundReason(className));
    }


    private String printExceptionInfo() {
        StringBuilder stringBuilder = new StringBuilder(" installed bundles: ");
        List<Bundle> bundles = Framework.getBundles();
        if (!(bundles == null || bundles.isEmpty())) {
            for (Bundle bundle : Framework.getBundles()) {

                stringBuilder.append(bundle.getLocation());

                stringBuilder.append(":");
            }
        }
        return stringBuilder.toString();
    }
}
