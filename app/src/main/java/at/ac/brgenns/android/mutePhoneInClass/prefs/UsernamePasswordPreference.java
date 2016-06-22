package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import at.ac.brgenns.android.mutePhoneInClass.AccountAuthenticatorService;
import at.ac.brgenns.android.mutePhoneInClass.MutePhoneService;
import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 22.06.2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class UsernamePasswordPreference extends DialogPreference {

    private String userName;
    private String password;
    private EditText usernameInput;
    private EditText passwordInput;

    public UsernamePasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.password_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        usernameInput = (EditText) view.findViewById(R.id.userNameInput);
        usernameInput.setText(userName);
        passwordInput = (EditText) view.findViewById(R.id.passwordInput);
        passwordInput.setHint(R.string.password);
    }

    public void show() {
        showDialog(null);
    }

    @Override
//    @TargetApi(Build.VERSION_CODES.M)
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            userName = String.valueOf(usernameInput.getText());
            password = String.valueOf(passwordInput.getText());
            if (userName != null && password != null) {
                Account account = new Account(userName, AccountAuthenticatorService.AUTH_TYPE);
                AccountManager accountManager = AccountManager.get(this.getContext());
                boolean success = accountManager.addAccountExplicitly(account, password, null);
                if (success) {
                    persistString(userName);
                }
            }
        }
        Intent mutePhoneService = new Intent(this.getContext(), MutePhoneService.class);
        mutePhoneService.putExtra(MutePhoneService.TASK, MutePhoneService.KUSS_ACCOUNT);
        this.getContext().startService(mutePhoneService);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String def = (defaultValue instanceof String) ? (String) defaultValue
                : (defaultValue != null) ? defaultValue.toString() : "";
        if (restorePersistedValue) {
            this.userName = getPersistedString(def);
        } else {
            this.userName = (String) defaultValue;
        }

    }
}
