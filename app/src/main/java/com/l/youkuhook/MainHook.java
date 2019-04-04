package com.l.youkuhook;

import android.app.Application;
import android.content.Context;

import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.contains("com.youku.phone")) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    XposedBridge.log("find youku");

                    ClassLoader classLoader = ((Context) param.args[0]).getClassLoader();

                    //isvip
                    Class<?> fClazz = classLoader.loadClass("com.youku.player.a.f");

                    if (fClazz != null) {
                        XposedHelpers.findAndHookMethod(fClazz, "isVip", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(true);
                            }
                        });
                    }

                    //mid ad
                    Class<?> xadsdk_e_cClazz = classLoader.loadClass("com.xadsdk.e.c");

                    if (xadsdk_e_cClazz != null) {
                        XposedHelpers.findAndHookMethod(xadsdk_e_cClazz, "getAdvItem", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(null);
                            }
                        });
                    }

                    //mid ad fail
                    Class<?> xadsdk_aClazz = classLoader.loadClass("com.xadsdk.b");

                    if (xadsdk_aClazz != null) {
                        XposedHelpers.findAndHookMethod(xadsdk_aClazz, "MU", String.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                param.args[0] = "";
                            }
                        });
                    }

                    //ad
                    Class<?> videoInfoClazz = classLoader.loadClass("com.youku.upsplayer.module.VideoInfo");

                    if (videoInfoClazz != null) {
                        XposedHelpers.findAndHookMethod(videoInfoClazz, "getAd", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(null);
                            }
                        });
                    }

                    //ssl socket factory
                    Class<?> cClazz = classLoader.loadClass("com.youku.upsplayer.a.c");
                    Class<?> connectionClazz = classLoader.loadClass("java.net.HttpURLConnection");
                    Class<?> urlClazz = classLoader.loadClass("java.net.URL");

                    if (cClazz != null) {
                        XposedHelpers.findAndHookMethod(cClazz, "a", connectionClazz, urlClazz, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);

                                SSLSocketFactory sslSocketFactory = systemDefaultSslSocketFactory(sysDefaultTrustManager());

                                HttpsURLConnection connection = (HttpsURLConnection) param.args[0];
                                connection.setSSLSocketFactory(sslSocketFactory);
                                param.args[0] = connection;

                                XposedBridge.log("after hook douyin a");
                            }
                        });
                    }
                }
            });
        }
    }

    private SSLSocketFactory systemDefaultSslSocketFactory(X509TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    private X509TrustManager sysDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalAccessException("unexpected dafault trust manager");
            }
            return (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
