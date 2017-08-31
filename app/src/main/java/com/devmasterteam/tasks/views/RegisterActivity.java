package com.devmasterteam.tasks.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.devmasterteam.tasks.R;
import com.devmasterteam.tasks.infra.operation.OperationListener;
import com.devmasterteam.tasks.manager.PersonManager;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewHolder mViewHolder = new ViewHolder();
    private PersonManager mPersonManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Incializa as variáveis
        this.mContext = this;
        this.mPersonManager = new PersonManager(this.mContext);

        // Inicializa elementos
        this.mViewHolder.imageBack = (ImageView) this.findViewById(R.id.image_toolbar_back);
        this.mViewHolder.editName = (EditText) this.findViewById(R.id.edit_name);
        this.mViewHolder.editEmail = (EditText) this.findViewById(R.id.edit_email);
        this.mViewHolder.editPassword = (EditText) this.findViewById(R.id.edit_password);
        this.mViewHolder.buttonSave = (Button) this.findViewById(R.id.button_save);

        // Inicializa eventos
        this.mViewHolder.buttonSave.setOnClickListener(this);
        this.mViewHolder.imageBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_save) {
            this.handleSave();
        } else if (id == R.id.image_toolbar_back) {
            super.onBackPressed();
        }
    }

    /**
     * Salva usuário
     */
    private void handleSave() {
        String email = this.mViewHolder.editEmail.getText().toString();
        String password = this.mViewHolder.editPassword.getText().toString();
        String name = this.mViewHolder.editName.getText().toString();

        // Cria usuário
        this.mPersonManager.create(email, password, name, registerListener());
    }

    /**
     * Listener de registro
     */
    private OperationListener<Boolean> registerListener() {
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
        private ImageView imageBack;
        private EditText editName;
        private EditText editEmail;
        private EditText editPassword;
        private Button buttonSave;
    }

}
