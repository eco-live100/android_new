package com.app.ecolive.msg_module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.app.ecolive.R;

import com.app.ecolive.databinding.SendCallLayoutBinding;
import com.google.android.material.textfield.TextInputLayout;
 import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.List;


public class ZegoSendCallButton extends FrameLayout {

    private SendCallLayoutBinding mBinding;
    private String userId;
    private String userName;
    public ZegoSendCallButton(Context context, String userId, String userName) {
        super(context);
        this.userId = userId;
        this.userName = userName;
        initView(context);
    }

    public ZegoSendCallButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ZegoSendCallButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.send_call_layout, this, true);
        initVoiceButton();
        initVideoButton();
    }

    private void initVideoButton() {
       /* mBinding.newVideoCall.setIsVideoCall(true);
        mBinding.newVideoCall.setOnClickListener(v -> {
            List<ZegoUIKitUser> users = new ArrayList<>();
            users.add(new ZegoUIKitUser(userId, userName));
            mBinding.newVideoCall.setInvitees(users);
        });*/

        ZegoSendCallInvitationButton newVideoCall = mBinding.newVideoCall;
        newVideoCall.setIsVideoCall(true);

        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //        newVideoCall.setResourceID("zegouikit_call");
        newVideoCall.setResourceID("Eco_live");
        newVideoCall.showErrorToast(false);
        newVideoCall.setOnClickListener(v -> {
//            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
//            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = userId.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVideoCall.setInvitees(users);
        });
    }

    private void initVoiceButton() {
        /*mBinding.newVoiceCall.setIsVideoCall(false);
        mBinding.newVoiceCall.setOnClickListener(v -> {
            List<ZegoUIKitUser> users = new ArrayList<>();
            users.add(new ZegoUIKitUser(userId, userName));
            mBinding.newVoiceCall.setInvitees(users);
        });*/
        ZegoSendCallInvitationButton newVoiceCall = mBinding.newVoiceCall;
        newVoiceCall.setIsVideoCall(false);

        newVoiceCall.showErrorToast(false);

        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //        newVoiceCall.setResourceID("zegouikit_call");
        newVoiceCall.setResourceID("Eco_live");

        newVoiceCall.setOnClickListener(v -> {
//            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
//            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = userId.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {

                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVoiceCall.setInvitees(users);
        });
    }
}
