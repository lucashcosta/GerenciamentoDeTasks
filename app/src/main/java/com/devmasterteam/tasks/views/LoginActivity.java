package com.devmasterteam.tasks.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devmasterteam.tasks.R;
import com.devmasterteam.tasks.constants.TaskConstants;
import com.devmasterteam.tasks.infra.operation.OperationListener;
import com.devmasterteam.tasks.infra.security.SecurityPreferences;
import com.devmasterteam.tasks.manager.PersonManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewHolder mViewHolder = new ViewHolder();
    private PersonManager mPersonManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Incializa as variáveis
        this.mContext = this;
        this.mPersonManager = new PersonManager(this.mContext);

        // Inicializa elementos
        this.mViewHolder.editEmail = (EditText) this.findViewById(R.id.edit_email);
        this.mViewHolder.editPassword = (EditText) this.findViewById(R.id.edit_password);
        this.mViewHolder.buttonLogin = (Button) this.findViewById(R.id.button_login);
        this.mViewHolder.textRegister = (TextView) this.findViewById(R.id.text_register);

        // Inicializa eventos
        this.mViewHolder.buttonLogin.setOnClickListener(this);
        this.mViewHolder.textRegister.setOnClickListener(this);

        // Verifica se usuário está logado
        this.verifyLoggedUser();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_login) {
            this.handleLogin();
        } else if (id == R.id.text_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    /**
     * Salva usuário
     */
    private void handleLogin() {
        String email = this.mViewHolder.editEmail.getText().toString();
        String password = this.mViewHolder.editPassword.getText().toString();

        // Cria usuário
        this.mPersonManager.login(email, password, loginListener());
    }

    /**
     * Verifica se usuário está logado
     */
    private void verifyLoggedUser() {
        SecurityPreferences securityPreferences = new SecurityPreferences(this.mContext);
        String tokenKey = securityPreferences.getStoredString(TaskConstants.HEADER.TOKEY_KEY);
        String personKey = securityPreferences.getStoredString(TaskConstants.HEADER.PERSON_KEY);

        if (!"".equals(tokenKey) && !"".equals(personKey)) {
            startActivity(new Intent(this.mContext, MainActivity.class));

            // Impede que o usuário volte a essa tela
            finish();
        }
    }

    /**
     * Listener de login
     */
    private OperationListener<Boolean> loginListener() {
        return new OperationListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }

            @Override
            public void onError(int error, String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        };
    }

    /**
     * ViewHolder
     */
    private static class ViewHolder {
        private EditText editEmail;
        private EditText editPassword;
        private Button buttonLogin;
        private TextView textRegister;
    }
}
