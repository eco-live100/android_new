package com.app.ecolive.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.zimkit.services.ZIMKit;


public class AppLifecycleObserver implements DefaultLifecycleObserver {

    public static final String TAG = AppLifecycleObserver.class.getName();

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onResume(owner);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        ZIMKit.disconnectUser();
        ZegoUIKitPrebuiltCallInvitationService.unInit();

    }
}