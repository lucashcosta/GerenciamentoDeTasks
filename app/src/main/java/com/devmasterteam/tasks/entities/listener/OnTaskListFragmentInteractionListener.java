package com.devmasterteam.tasks.entities.listener;

public interface OnTaskListFragmentInteractionListener {

    /**
     * Click para edição
     */
    void onListClick(int taskId);

    /**
     * Remoção
     */
    void onDeleteClick(int taskId);

    /**
     * Completa tarefa
     */
    void onCompleteClick(int taskId);

    /**
     * Descompleta tarefa
     */
    void onUncompleteClick(int taskId);

}
