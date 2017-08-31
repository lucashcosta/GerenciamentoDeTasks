package com.devmasterteam.tasks.views;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devmasterteam.tasks.R;
import com.devmasterteam.tasks.constants.TaskConstants;
import com.devmasterteam.tasks.entities.PriorityEntity;
import com.devmasterteam.tasks.entities.TaskEntity;
import com.devmasterteam.tasks.infra.operation.OperationListener;
import com.devmasterteam.tasks.manager.PriorityManager;
import com.devmasterteam.tasks.manager.TaskManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskFormActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private ViewHolder mViewHolder = new ViewHolder();
    private TaskManager mTaskManager;
    private PriorityManager mPriorityManager;
    private Context mContext;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private int mTaskId = 0;
    private List<PriorityEntity> mPriorityEntityList;
    private List<Integer> mPriorityEntityListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getSupportActionBar().hide();

        setContentView(R.layout.activity_task_form);

        // Inicializa variáveis
        this.mViewHolder.imageToolbarBack = (ImageView) this.findViewById(R.id.image_toolbar_back);
        this.mViewHolder.textToolbar = (TextView) this.findViewById(R.id.text_toolbar);
        this.mViewHolder.editDescription = (EditText) this.findViewById(R.id.edit_description);
        this.mViewHolder.checkComplete = (CheckBox) this.findViewById(R.id.check_complete);
        this.mViewHolder.spinnerPriority = (Spinner) this.findViewById(R.id.spinner_priority);
        this.mViewHolder.buttonDate = (Button) this.findViewById(R.id.button_date);
        this.mViewHolder.buttonSave = (Button) this.findViewById(R.id.button_save);
        this.mViewHolder.progressDialog = new ProgressDialog(this);
        this.mContext = this;

        // Atribui eventos
        this.mViewHolder.buttonSave.setOnClickListener(this);
        this.mViewHolder.buttonDate.setOnClickListener(this);
        this.mViewHolder.imageToolbarBack.setOnClickListener(this);

        // Variáveis
        this.mTaskManager = new TaskManager(this);
        this.mPriorityManager = new PriorityManager(this);
        this.mPriorityEntityList = new ArrayList<>();
        this.mPriorityEntityListId = new ArrayList<>();

        // Carrega valores
        this.loadPriorities();

        // Carrega dados passados para a activity
        this.loadDataFromActivity();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.button_save) {
            this.handleSave();
        } else if (id == R.id.button_date) {
            this.showDateDialog();
        } else if (id == R.id.image_toolbar_back) {
            this.onBackPressed();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        String strDate = SIMPLE_DATE_FORMAT.format(calendar.getTime());
        this.mViewHolder.buttonDate.setText(strDate);
    }

    /**
     * Mostra datepicker de seleção
     */
    private void showDateDialog() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, this, year, month, day).show();
    }

    /**
     * Trata click
     */
    private void handleSave() {

        // Loading
        this.showLoading(true, getString(R.string.salvando), getString(R.string.salvando_tarefa));

        try {
            TaskEntity taskEntity = new TaskEntity();

            taskEntity.Id = this.mTaskId;
            taskEntity.Description = this.mViewHolder.editDescription.getText().toString();
            taskEntity.PriorityId = this.mPriorityEntityListId.get(this.mViewHolder.spinnerPriority.getSelectedItemPosition());
            taskEntity.Complete = this.mViewHolder.checkComplete.isChecked();

            if (!"".equals(this.mViewHolder.buttonDate.getText()))
                taskEntity.DueDate = SIMPLE_DATE_FORMAT.parse(this.mViewHolder.buttonDate.getText().toString());

            if (this.mTaskId == 0) {
                this.mTaskManager.insert(taskEntity, taskSavedListener());
            } else {
                this.mTaskManager.update(taskEntity, taskSavedListener());
            }

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.UNEXPECTED_ERROR), Toast.LENGTH_LONG).show();
            this.showLoading(false, "", "");
        }
    }

    /**
     * Carrega dados de edição
     */
    private void loadDataFromActivity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mTaskId = bundle.getInt(TaskConstants.BUNDLE.TASK_ID, 0);

            // Carrega tarefa
            if (this.mTaskId != 0) {
                this.mViewHolder.textToolbar.setText(R.string.atualizar_tarefa);
                this.mViewHolder.buttonSave.setText(R.string.atualizar_tarefa_button);
                this.mTaskManager.get(this.mTaskId, taskLoadedListener());
            }

        }
    }

    /**
     * Carrega prioridades
     */
    private void loadPriorities() {

        // Lista de prioridades do banco de dados local
        this.mPriorityEntityList = this.mPriorityManager.getListLocal();

        List<String> listPriorities = new ArrayList<>();
        for (PriorityEntity priorityEntity : this.mPriorityEntityList) {
            listPriorities.add(priorityEntity.Description);
            this.mPriorityEntityListId.add(priorityEntity.Id);
        }

        // Cria adapter e usa no elemento
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listPriorities);
        this.mViewHolder.spinnerPriority.setAdapter(adapter);

    }

    /**
     * Obtém o indexo do valor carregado
     */
    private int getIndex(int priorityId) {
        int index = 0;
        for (int i = 0; i < this.mPriorityEntityList.size(); i++) {
            if (this.mPriorityEntityList.get(i).Id == priorityId) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Mostra ou esconde loading de frases enquanto consulta no servidor é feita
     */
    private void showLoading(Boolean show, String title, String message) {
        if (show) {
            this.mViewHolder.progressDialog.setTitle(title);
            this.mViewHolder.progressDialog.setMessage(message);
            this.mViewHolder.progressDialog.show();
        } else {
            this.mViewHolder.progressDialog.hide();
            this.mViewHolder.progressDialog.dismiss();
        }
    }

    /**
     * Listener quando a tarefa é carregada da API
     */
    private OperationListener<TaskEntity> taskLoadedListener() {
        return new OperationListener<TaskEntity>() {

            @Override
            public void onSuccess(TaskEntity result) {
                mViewHolder.editDescription.setText(result.Description);
                mViewHolder.buttonDate.setText(SIMPLE_DATE_FORMAT.format(result.DueDate));
                mViewHolder.checkComplete.setChecked(result.Complete);
                mViewHolder.spinnerPriority.setSelection(getIndex(result.PriorityId));
            }

            @Override
            public void onError(int error, String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        };
    }

    /**
     * Listener quando a tarefa é salva
     */
    private OperationListener<Boolean> taskSavedListener() {
        return new OperationListener<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {

                // Esconde mensagem
                showLoading(false, "", "");

                // Notifica o usuário
                if (mTaskId != 0) {
                    Toast.makeText(mContext, R.string.tarefa_atualizada_com_sucesso, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, R.string.tarefa_incluida_com_sucesso, Toast.LENGTH_LONG).show();
                }

                // Finaliza activity
                finish();
            }

            @Override
            public void onError(int error, String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                showLoading(false, "", "");
            }
        };
    }

    /**
     * ViewHolder
     */
    private static class ViewHolder {
        private ImageView imageToolbarBack;
        private TextView textToolbar;
        private EditText editDescription;
        private CheckBox checkComplete;
        private Spinner spinnerPriority;
        private Button buttonDate;
        private Button buttonSave;
        private ProgressDialog progressDialog;
    }

}