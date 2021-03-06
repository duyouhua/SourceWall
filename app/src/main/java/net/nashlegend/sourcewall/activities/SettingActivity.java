package net.nashlegend.sourcewall.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.nashlegend.sourcewall.R;
import net.nashlegend.sourcewall.events.LoginStateChangedEvent;
import net.nashlegend.sourcewall.request.api.UserAPI;
import net.nashlegend.sourcewall.util.Config;
import net.nashlegend.sourcewall.util.Consts;
import net.nashlegend.sourcewall.util.Consts.ImageLoadMode;
import net.nashlegend.sourcewall.util.Consts.Keys;
import net.nashlegend.sourcewall.util.Consts.TailType;
import net.nashlegend.sourcewall.util.Mob;
import net.nashlegend.sourcewall.util.PrefsUtil;
import net.nashlegend.sourcewall.util.UrlCheckUtil;

import de.greenrobot.event.EventBus;

public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private TextView imageText;
    private TextView logText;
    private View tailsView;
    private View modesView;
    private ImageView tailArrow;
    private ImageView modeArrow;

    private RadioButton buttonAlways;
    private RadioButton buttonNever;
    private RadioButton buttonWifi;
    private CheckBox checkBox;

    private RadioButton buttonDefault;
    private RadioButton buttonPhone;
    private RadioButton buttonCustom;
    private EditText tailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        View imageModeView = findViewById(R.id.layout_image_mode);
        View customTailView = findViewById(R.id.layout_custom_tail);
        View logInOutView = findViewById(R.id.layout_log_in_out);
        View aboutView = findViewById(R.id.layout_about_app);
        imageText = (TextView) findViewById(R.id.text_image_mode);
        logText = (TextView) findViewById(R.id.text_log_in_out);
        tailsView = findViewById(R.id.layout_tails);
        modesView = findViewById(R.id.layout_modes);
        tailArrow = (ImageView) findViewById(R.id.image_tail_arrow);
        modeArrow = (ImageView) findViewById(R.id.image_mode_arrow);

        buttonDefault = (RadioButton) findViewById(R.id.button_use_default);
        buttonPhone = (RadioButton) findViewById(R.id.button_use_phone);
        buttonCustom = (RadioButton) findViewById(R.id.button_use_custom);
        tailText = (EditText) findViewById(R.id.text_tail);

        buttonAlways = (RadioButton) findViewById(R.id.button_always_load);
        buttonNever = (RadioButton) findViewById(R.id.button_never_load);
        buttonWifi = (RadioButton) findViewById(R.id.button_wifi_only);
        checkBox = (CheckBox) findViewById(R.id.check_homepage);

        buttonDefault.setOnCheckedChangeListener(this);
        buttonPhone.setOnCheckedChangeListener(this);
        buttonCustom.setOnCheckedChangeListener(this);

        buttonAlways.setOnCheckedChangeListener(this);
        buttonNever.setOnCheckedChangeListener(this);
        buttonWifi.setOnCheckedChangeListener(this);

        checkBox.setOnCheckedChangeListener(this);

        imageModeView.setOnClickListener(this);
        customTailView.setOnClickListener(this);
        logInOutView.setOnClickListener(this);
        aboutView.setOnClickListener(this);
        tailsView.setVisibility(View.GONE);
        modesView.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (UserAPI.isLoggedIn()) {
            logText.setText(R.string.log_out);
        } else {
            logText.setText(R.string.log_in);
        }

        checkBox.setChecked(PrefsUtil.readBoolean(Keys.Key_Image_No_Load_Homepage, false));

        int mode = Config.getImageLoadMode();
        switch (mode) {
            case ImageLoadMode.MODE_ALWAYS_LOAD:
                imageText.setText(R.string.mode_always_load);
                buttonAlways.setChecked(true);
                checkBox.setEnabled(true);
                break;
            case ImageLoadMode.MODE_NEVER_LOAD:
                imageText.setText(R.string.mode_never_load);
                buttonNever.setChecked(true);
                checkBox.setEnabled(false);
                break;
            case ImageLoadMode.MODE_LOAD_WHEN_WIFI:
                imageText.setText(R.string.mode_load_load_when_wifi);
                buttonWifi.setChecked(true);
                checkBox.setEnabled(true);
                break;
        }

        switch (PrefsUtil.readInt(Keys.Key_Use_Tail_Type, TailType.Type_Use_Default_Tail)) {
            case TailType.Type_Use_Default_Tail:
                buttonDefault.setChecked(true);
                tailText.setText(Config.getDefaultPlainTail());
                tailText.setEnabled(false);
                break;
            case TailType.Type_Use_Phone_Tail:
                buttonPhone.setChecked(true);
                tailText.setText(Config.getPhonePlainTail());
                tailText.setEnabled(false);
                break;
            case TailType.Type_Use_Custom_Tail:
                buttonCustom.setChecked(true);
                tailText.setText(PrefsUtil.readString(Keys.Key_Custom_Tail, ""));
                tailText.setEnabled(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_image_mode:
                popupImageMode();
                break;
            case R.id.layout_custom_tail:
                toggleCustomTailLayout();
                break;
            case R.id.layout_log_in_out:
                toggleLoginState();
                break;
            case R.id.layout_about_app:
                showAboutApp();
                break;
        }
    }

    private void popupImageMode() {
        if (modesView.getVisibility() == View.VISIBLE) {
            modesView.setVisibility(View.GONE);
            modeArrow.setRotation(0);
        } else {
            modesView.setVisibility(View.VISIBLE);
            modeArrow.setRotation(-90);
        }
    }

    private void toggleCustomTailLayout() {
        if (tailsView.getVisibility() == View.VISIBLE) {
            tailsView.setVisibility(View.GONE);
            tailArrow.setRotation(0);
        } else {
            tailsView.setVisibility(View.VISIBLE);
            tailArrow.setRotation(-90);
        }
    }

    private void toggleLoginState() {
        if (UserAPI.isLoggedIn()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.ok_to_logout)
                    .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserAPI.logout();
                            MobclickAgent.onEvent(SettingActivity.this, Mob.Event_Logout);
                            logText.setText(R.string.log_in);
                            EventBus.getDefault().post(new LoginStateChangedEvent());
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
            dialog.show();
        } else {
            startOneActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void showAboutApp() {
        UrlCheckUtil.openWithBrowser("https://github.com/NashLegend/SourceWall/blob/master/README.md");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.check_homepage) {
            PrefsUtil.saveBoolean(Keys.Key_Image_No_Load_Homepage, isChecked);
        } else {
            if (isChecked) {
                switch (buttonView.getId()) {
                    case R.id.button_use_default:
                        tailText.setText(Config.getDefaultPlainTail());
                        tailText.setEnabled(false);
                        break;
                    case R.id.button_use_phone:
                        tailText.setText(Config.getPhonePlainTail());
                        tailText.setEnabled(false);
                        break;
                    case R.id.button_use_custom:
                        String cus = PrefsUtil.readString(Keys.Key_Custom_Tail, "");
                        if (!TextUtils.isEmpty(cus)) {
                            tailText.setText(cus);
                        }
                        tailText.setEnabled(true);
                        break;
                    case R.id.button_always_load:
                        imageText.setText(R.string.mode_always_load);
                        checkBox.setEnabled(true);
                        break;
                    case R.id.button_never_load:
                        imageText.setText(R.string.mode_never_load);
                        checkBox.setEnabled(false);
                        break;
                    case R.id.button_wifi_only:
                        imageText.setText(R.string.mode_load_load_when_wifi);
                        checkBox.setEnabled(true);
                        break;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        String preTail = Config.getSimpleReplyTail();
        if (buttonDefault.isChecked()) {
            PrefsUtil.saveInt(Keys.Key_Use_Tail_Type, TailType.Type_Use_Default_Tail);
        } else if (buttonPhone.isChecked()) {
            PrefsUtil.saveInt(Keys.Key_Use_Tail_Type, TailType.Type_Use_Phone_Tail);
        } else {
            PrefsUtil.saveInt(Keys.Key_Use_Tail_Type, TailType.Type_Use_Custom_Tail);
            PrefsUtil.saveString(Keys.Key_Custom_Tail, tailText.getText().toString());
        }
        String crtTail = Config.getSimpleReplyTail();

        if (crtTail.equals(preTail)) {
            MobclickAgent.onEvent(this, Mob.Event_Modify_Tail);
        }

        if (buttonAlways.isChecked()) {
            PrefsUtil.saveInt(Keys.Key_Image_Load_Mode, ImageLoadMode.MODE_ALWAYS_LOAD);
        } else if (buttonNever.isChecked()) {
            PrefsUtil.saveInt(Keys.Key_Image_Load_Mode, ImageLoadMode.MODE_NEVER_LOAD);
        } else {
            PrefsUtil.saveInt(Keys.Key_Image_Load_Mode, ImageLoadMode.MODE_LOAD_WHEN_WIFI);
        }
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
