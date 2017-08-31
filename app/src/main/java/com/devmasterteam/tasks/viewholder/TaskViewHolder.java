package com.devmasterteam.tasks.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmasterteam.tasks.R;
import com.devmasterteam.tasks.constants.PriorityCacheConstants;
import com.devmasterteam.tasks.entities.TaskEntity;
import com.devmasterteam.tasks.entities.listener.OnTaskListFragmentInteractionListener;

import java.text.SimpleDateFormat;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextDescription;
    private TextView mTextPriority;
    private TextView mTextDueDate;
    private ImageView mImageTask;
    private Context mContext;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public TaskViewHolder(View itemView, Context context) {
        super(itemView);
        this.mContext = context;
        this.mTextDescription = itemView.findViewById(R.id.text_description);
        this.mTextPriority = itemView.findViewById(R.id.text_priority);
        this.mTextDueDate = itemView.findViewById(R.id.text_due_date);
        this.mImageTask = itemView.findViewById(R.id.image_task);
    }

    public void bindData(final TaskEntity taskEntity, final OnTaskListFragmentInteractionListener listener) {

        // Atribui valores
        this.mTextDescription.setText(taskEntity.Description);
        this.mTextPriority.setText(PriorityCacheConstants.getPriorityDescription(taskEntity.PriorityId));
        this.mTextDueDate.setText(SIMPLE_DATE_FORMAT.format(taskEntity.DueDate));

        // Atribui evento de click de detalhes
        this.mTextDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onListClick(taskEntity.Id);
            }
        });

        // Atribui evento de remoção
        this.mTextDescription.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialogConfirmation(taskEntity, listener);
                return true;
            }
        });

        this.mImageTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskEntity.Complete) {
                    listener.onUncompleteClick(taskEntity.Id);
                } else {
                    listener.onCompleteClick(taskEntity.Id);
                }
            }
        });

        // Faz o tratamento para tarefas já completas
        if (taskEntity.Complete) {
            this.mTextDescription.setTextColor(Color.GRAY);
            this.mImageTask.setImageResource(R.drawable.ic_done);
        }

    }

    /**
     * Confirma remoção
     */
    private void showDialogConfirmation(final TaskEntity taskEntity, final OnTaskListFragmentInteractionListener listener) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.remocao_de_tarefa)
                .setMessage("Deseja realmente remover " + taskEntity.Description + "?")
                // .setIcon(R.drawable.ic_remove)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        listener.onDeleteClick(taskEntity.Id);
                    }
                })
                .setNegativeButton(R.string.cancelar, null).show();
    }

}
