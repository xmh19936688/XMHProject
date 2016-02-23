package com.xmh.skyeyedemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.utils.CommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_username)EditText etUsername;
    @Bind(R.id.et_password)EditText etPassword;
    @Bind(R.id.btn_login)Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    void onLoginClick(View view){
        CommonUtils.closeInputMethod(this);
        //region 检查网络连接
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        //endregion
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        //region 检查输入合法性
        if (TextUtils.isEmpty(username)) {
            Snackbar.make(getWindow().getDecorView(), R.string.User_name_cannot_be_empty, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Snackbar.make(getWindow().getDecorView(), R.string.Password_cannot_be_empty, Snackbar.LENGTH_SHORT).show();
            return;
        }
        //endregion

        //region 显示登录对话框
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();
        //endregion
        EMChatManager.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Snackbar.make(getWindow().getDecorView(), R.string.login_success, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Snackbar.make(getWindow().getDecorView(), R.string.login_fail, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

}