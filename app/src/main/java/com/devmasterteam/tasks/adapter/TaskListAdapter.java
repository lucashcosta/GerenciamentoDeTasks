package com.devmasterteam.tasks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devmasterteam.tasks.R;
import com.devmasterteam.tasks.entities.TaskEntity;
import com.devmasterteam.tasks.entities.listener.OnTaskListFragmentInteractionListener;
import com.devmasterteam.tasks.viewholder.TaskViewHolder;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private OnTaskListFragmentInteractionListener mOnTaskListFragmentInteractionListener;
    private List<TaskEntity> mListTaskEntities;

    /**
     * Construtor
     */
    public TaskListAdapter(List<TaskEntity> taskList, OnTaskListFragmentInteractionListener listener) {
        this.mListTaskEntities = taskList;
        this.mOnTaskListFragmentInteractionListener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        // Infla o layout da linha e faz uso na listagem
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_task_list, parent, false);

        return new TaskViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        // Obtém item da lista
        final TaskEntity taskEntity = this.mListTaskEntities.get(position);
        holder.bindData(taskEntity, this.mOnTaskListFragmentInteractionListener);

    }

    @Override
    public int getItemCount() {
        return this.mListTaskEntities.size();
    }

}
