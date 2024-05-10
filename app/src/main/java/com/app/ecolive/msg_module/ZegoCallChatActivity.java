package com.app.ecolive.msg_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.ecolive.R;
import com.app.ecolive.databinding.ActivityZegoCallChatBinding;
import com.app.ecolive.databinding.ChatlistActivityBinding;
import com.app.ecolive.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener;
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener;
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener;
import com.zegocloud.uikit.prebuilt.call.event.ZegoCallEndReason;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler;
import com.zegocloud.zimkit.common.ZIMKitRouter;
import com.zegocloud.zimkit.common.enums.ZIMKitConversationType;
import com.zegocloud.zimkit.components.message.interfaces.ZIMKitMessagesListListener;
import com.zegocloud.zimkit.components.message.model.ZIMKitHeaderBar;
import com.zegocloud.zimkit.components.message.ui.ZIMKitMessageFragment;
import com.zegocloud.zimkit.services.ZIMKit;
import com.zegocloud.zimkit.services.callback.CreateGroupCallback;
import com.zegocloud.zimkit.services.callback.JoinGroupCallback;
import com.zegocloud.zimkit.services.model.ZIMKitGroupInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMErrorUserInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMConversationType;
import im.zego.zim.enums.ZIMErrorCode;
import timber.log.Timber;

public class ZegoCallChatActivity extends AppCompatActivity {

     ActivityZegoCallChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_zego_call_chat);
        binding.toolbar.toolbarTitle.setText("All Chats");
        Utils.Companion.changeStatusColor(this, R.color.color_050D4C);
        Utils.Companion.changeStatusTextColor(this);
        binding.toolbar.ivBack.setOnClickListener(view -> {
            finish();
        });
        binding.allUser.setOnClickListener(view->{
            startActivity(new Intent(this,AllUserActivity.class));
        });
        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason((scope, deniedList) -> {
                    String message = "We need your consent for the following permissions in order to use the offline call function properly";
                    scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                }).request((allGranted, grantedList, deniedList) -> {
                });

        ZIMKit.registerMessageListListener(fragment -> {
            if (fragment != null) {
                if (fragment.getConversationType() == ZIMConversationType.PEER) {
                    String conversationID = fragment.getConversationID();
                    String conversationName = fragment.getConversationName();
                    ZegoSendCallButton sendCallButton = new ZegoSendCallButton(ZegoCallChatActivity.this, conversationID, conversationName);
                    ZIMKitHeaderBar headerBar = new ZIMKitHeaderBar();
                    headerBar.setRightView(sendCallButton);

                    return headerBar;
                }
            }
            return null;
        });


      //  initVoiceButton();

       // initVideoButton();
    }



    private void showNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Chat");

        EditText editText = new EditText(this);
        editText.setHint("User ID");
        builder.setView(editText);

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            String userID = editText.getText().toString();
            startChat(userID, ZIMKitConversationType.ZIMKitConversationTypePeer);
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void startChat(String conversationID, ZIMKitConversationType type) {
        ZIMKitRouter.toMessageActivity(this, conversationID, type);
    }

 }