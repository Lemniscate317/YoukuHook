package com.l.youkuhook;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

                    //pre ad downloader
                    Class<?> download_v2_z_clazz = classLoader.loadClass("com.youku.service.download.v2.z");
                    Class<?> download_a_clazz = classLoader.loadClass("com.youku.service.download.a");

                    if (download_v2_z_clazz != null) {
                        XposedHelpers.findAndHookMethod(download_v2_z_clazz, "F", download_a_clazz, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                HashMap<String, String> hashMap = (HashMap<String, String>) param.getResult();
                                hashMap.put("vid", "");
                                hashMap.put("vip", "1");
                                param.setResult(hashMap);
                            }
                        });
                    }

                    //download v2 ab
                    Class<?> v2_ab_clazz = classLoader.loadClass("com.youku.service.download.v2.ab");
                    Class<?> adFileModel_clazz = classLoader.loadClass("com.youku.xadsdk.pluginad.model.AdFileModel");

                    if (v2_ab_clazz != null) {
                        XposedHelpers.findAndHookMethod(v2_ab_clazz, "b", List.class, File.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                XposedBridge.log("v2_ab_clazz");
                                List list = new ArrayList<>();
                                param.args[0] = list;
                            }
                        });
                    }

//                    //url container
//                    final Class<?> b_b_clazz = classLoader.loadClass("com.xadsdk.c.b.b");
//
//                    if (b_b_clazz != null) {
//                        XposedHelpers.findAndHookMethod(b_b_clazz, "setDebugMode", boolean.class, new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                XposedBridge.log("b_b_clazz");
//                                Field iyd = b_b_clazz.getDeclaredField("iyd");
//                                Object o = iyd.getType().newInstance();
//                                iyd.set("", o);
//                                Field iyc = b_b_clazz.getDeclaredField("iyc");
//                                Object iycObj = iyc.getType().newInstance();
//                                iyc.set("", iycObj);
//                            }
//                        });
//                    }

                    //url
//                    final Class<?> network_l_clazz = classLoader.loadClass("com.youku.network.l");
//
//                    if (network_l_clazz != null) {
//                        XposedBridge.log("network_l_clazz");
//                        Field adv_banner = network_l_clazz.getDeclaredField("YOUKU_ADV_BANNER");
//                        Object o = adv_banner.getType().newInstance();
//                        adv_banner.set("www", o);
//
//                        Field ad_domain = network_l_clazz.getDeclaredField("YOUKU_AD_DOMAIN");
//                        Object iycObj = ad_domain.getType().newInstance();
//                        ad_domain.set("www", iycObj);
//                    }

                    //ad enable
                    final Class<?> adEnableConfig_clazz = classLoader.loadClass("com.youku.xadsdk.config.AdEnableConfig");

                    if (adEnableConfig_clazz != null) {
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "init", int.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                param.args[0] = 2;
                            }
                        });

                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isPreImageAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isPreAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isMidAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isPauseAdEnable", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isCornerAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isSceneAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isBottomAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isCustomAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isSoftAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isPressFlowAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adEnableConfig_clazz, "isPostAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                    }

                    //device config
                    final Class<?> a_b_clazz = classLoader.loadClass("com.alimm.xadsdk.a.b");

                    if (a_b_clazz != null)

                    {
                        XposedHelpers.findAndHookMethod(a_b_clazz, "getImei", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult("123413413241234");
                            }
                        });
                        XposedHelpers.findAndHookMethod(a_b_clazz, "getAndroidId", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult("sak;fjlj;kjk;");
                            }
                        });
                        XposedHelpers.findAndHookMethod(a_b_clazz, "getMacAddress", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult("ssdfsdlj;kjk;");
                            }
                        });
                    }

                    //ad config center
                    Class<?> adConfigCenter_clazz = classLoader.loadClass("com.youku.xadsdk.config.AdConfigCenter");

                    if (adConfigCenter_clazz != null)

                    {
                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "isOfflinePreAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "isColdSplashAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "isHotSplashAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "isDownloadOptFeatureEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "limitLoopAdReq", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(true);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "isSoftAdEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });
                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "isClearOfflinePreAd", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(true);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "enableFeedMid", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "enableClick", int.class, int.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "isPreviewEnabled", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "enableFollowFeed", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "enableWebNavConfirm", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(false);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "getFeedAdMinHeight", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(0);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "getFeedAdMaxHeight", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(0);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "getFloatAdWebView", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(0);
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "getWeexUrl", int.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult("");
                            }
                        });

                        XposedHelpers.findAndHookMethod(adConfigCenter_clazz, "getMinAdpRequestInterval", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult(Integer.MAX_VALUE);
                            }
                        });
                    }

//                    //ad
//                    Class<?> cmsbase_dto_Clazz = classLoader.loadClass("com.youku.phone.cmsbase.dto");
//
//                    if (cmsbase_dto_Clazz != null) {
//                        XposedHelpers.findAndHookMethod(cmsbase_dto_Clazz, "getAd", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult("");
//                            }
//                        });
//                    }
//
//                    //ad
//                    Class<?> dto_component_Clazz = classLoader.loadClass("com.youku.phone.cmsbase.dto.component");
//
//                    if (dto_component_Clazz != null) {
//                        XposedHelpers.findAndHookMethod(dto_component_Clazz, "getAd", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult("");
//                            }
//                        });
//                    }
//
//                    //ad
//                    Class<?> feedadvideo_model_Clazz = classLoader.loadClass("com.alibaba.vase.petals.feedadvideo.model");
//
//                    if (feedadvideo_model_Clazz != null) {
//                        XposedHelpers.findAndHookMethod(feedadvideo_model_Clazz, "getAd", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult("");
//                            }
//                        });
//
//                        XposedHelpers.findAndHookMethod(feedadvideo_model_Clazz, "isExposed", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult(false);
//                            }
//                        });
//                    }
//
//                    //ad
//                    Class<?> ffeedadbottom_model_Clazz = classLoader.loadClass("com.alibaba.vase.petals.feedadbottom.model");
//
//                    if (ffeedadbottom_model_Clazz != null) {
//                        XposedHelpers.findAndHookMethod(ffeedadbottom_model_Clazz, "getAd", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult("");
//                            }
//                        });
//                    }
//
//                    //ad
//                    Class<?> feedadvideoview_model_Clazz = classLoader.loadClass("com.alibaba.vase.petals.feedadvideoview.model");
//
//                    if (feedadvideoview_model_Clazz != null) {
//                        XposedHelpers.findAndHookMethod(feedadvideoview_model_Clazz, "getAd", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult("");
//                            }
//                        });
//
//                        XposedHelpers.findAndHookMethod(feedadvideoview_model_Clazz, "isExposed", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult(false);
//                            }
//                        });
//                    }
//
//                    //ad
//                    Class<?> feedadview_model_Clazz = classLoader.loadClass("com.alibaba.vase.petals.feedadview.model");
//
//                    if (feedadview_model_Clazz != null) {
//                        XposedHelpers.findAndHookMethod(feedadview_model_Clazz, "getAd", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult("");
//                            }
//                        });
//
//                        XposedHelpers.findAndHookMethod(feedadview_model_Clazz, "isExposed", new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                super.afterHookedMethod(param);
//                                param.setResult(false);
//                            }
//                        });
//                    }

                    //ssl socket factory
                    Class<?> cClazz = classLoader.loadClass("com.youku.upsplayer.a.c");
                    Class<?> connectionClazz = classLoader.loadClass("java.net.HttpURLConnection");
                    Class<?> urlClazz = classLoader.loadClass("java.net.URL");

                    if (cClazz != null)

                    {
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
