package com.devmasterteam.tasks.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.devmasterteam.tasks.R;
import com.devmasterteam.tasks.constants.TaskConstants;
import com.devmasterteam.tasks.entities.TaskEntity;
import com.devmasterteam.tasks.adapter.TaskListAdapter;
import com.devmasterteam.tasks.entities.listener.OnTaskListFragmentInteractionListener;
import com.devmasterteam.tasks.infra.operation.OperationListener;
import com.devmasterteam.tasks.manager.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private int mFilter;
    private List<TaskEntity> mTaskEntityList;
    private TaskListAdapter mTaskListAdapter;
    private OnTaskListFragmentInteractionListener mOnTaskListFragmentInteractionListener;
    private TaskManager mTaskManager;
    private ViewHolder mViewHolder = new ViewHolder();
    private Boolean mMenuVisible = false;

    /**
     * Retorna instância da fragment
     */
    public static TaskListFragment newInstance(int filter) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt(TaskConstants.TASKFILTER.FILTER_KEY, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mFilter = getArguments().getInt(TaskConstants.TASKFILTER.FILTER_KEY, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Infla o layout
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Incializa as variáveis
        this.mContext = rootView.getContext();
        this.mTaskManager = new TaskManager(this.mContext);

        // Inicializa elementos
        this.mViewHolder.floatMenuButton = rootView.findViewById(R.id.float_menu_button);
        this.mViewHolder.floatAddTask = rootView.findViewById(R.id.float_add_task);

        // 1 - Obter a recyclerview
        this.mViewHolder.recylerTaskList = rootView.findViewById(R.id.recycler_task_list);

        // 2 - Definir adapter passando listagem de itens
        this.mTaskEntityList = new ArrayList<>();
        this.mTaskListAdapter = new TaskListAdapter(this.mTaskEntityList, this.mOnTaskListFragmentInteractionListener);
        this.mViewHolder.recylerTaskList.setAdapter(mTaskListAdapter);

        // 3 - Definir um layout
        this.mViewHolder.recylerTaskList.setLayoutManager(new LinearLayoutManager(this.mContext));

        // Inicializa eventos
        this.mViewHolder.floatMenuButton.setOnClickListener(this);
        this.mViewHolder.floatAddTask.setOnClickListener(this);

        // Inicializa listener
        this.createInteractionListener();

        // Retorna view
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.loadTasks();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.float_menu_button) {

            // Animação Floating Action Button como se fosse um menu.
            if (!this.mMenuVisible) {
                Animation animationShow = AnimationUtils.loadAnimation(this.mContext, R.anim.float_menu_show);
                mViewHolder.floatAddTask.startAnimation(animationShow);

                this.mViewHolder.floatAddTask.setVisibility(View.VISIBLE);
                this.mMenuVisible = true;
            } else {
                Animation animationHide = AnimationUtils.loadAnimation(this.mContext, R.anim.float_menu_hide);
                mViewHolder.floatAddTask.startAnimation(animationHide);

                this.mViewHolder.floatAddTask.setVisibility(View.GONE);
                this.mMenuVisible = false;
            }

        } else if (id == R.id.float_add_task) {
            Intent intent = new Intent(this.mContext, TaskFormActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Carrega tarefas
     */
    private void loadTasks() {
        this.mTaskEntityList = new ArrayList<>();
        this.mTaskManager.getList(this.mFilter, tasksLoaded());
    }

    /**
     * Interação com a listagem de tarefas
     */
    private void createInteractionListener() {
        this.mOnTaskListFragmentInteractionListener = new OnTaskListFragmentInteractionListener() {
            @Override
            public void onListClick(int taskId) {
                Bundle bundle = new Bundle();
                bundle.putInt(TaskConstants.BUNDLE.TASK_ID, taskId);

                Intent intent = new Intent(mContext, TaskFormActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int taskId) {
                mTaskManager.delete(taskId, taskDeletedListener());
            }

            @Override
            public void onCompleteClick(int taskId) {
                mTaskManager.complete(taskId, true, taskUpdatedListener());
            }

            @Override
            public void onUncompleteClick(int taskId) {
                mTaskManager.complete(taskId, false, taskUpdatedListener());
            }
        };
    }

    /**
     * Listener para quando a listagem de tarefas seja obtida
     */
    private OperationListener<List<TaskEntity>> tasksLoaded() {
        return new OperationListener<List<TaskEntity>>() {

            @Override
            public void onSuccess(List<TaskEntity> result) {
                mTaskEntityList.addAll(result);
                mTaskListAdapter = new TaskListAdapter(mTaskEntityList, mOnTaskListFragmentInteractionListener);
                mViewHolder.recylerTaskList.setAdapter(mTaskListAdapter);
                mTaskListAdapter.notifyDataSetChanged();

                int completed = 0;
                for (TaskEntity task : result) {
                    if (task.Complete)
                        completed++;
                }

                // Atualiza número de registros
                ((MainActivity) getActivity()).updateTaskCount(result.size(), completed);
            }

            @Override
            public void onError(int error, String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }

        };
    }

    /**
     * Listener para quando uma tarefa é removida
     */
    private OperationListener<Boolean> taskDeletedListener() {
        return new OperationListener<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(mContext, R.string.tarefa_removida_com_sucesso, Toast.LENGTH_LONG).show();
                loadTasks();
            }

            @Override
            public void onError(int error, String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }

        };
    }

    /**
     * Listener para quando uma tarefa é completa ou não completa
     */
    private OperationListener<Boolean> taskUpdatedListener() {
        return new OperationListener<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                loadTasks();
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
        private FloatingActionButton floatMenuButton;
        private FloatingActionButton floatAddTask;
        private RecyclerView recylerTaskList;
    }

}