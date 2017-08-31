package com.devmasterteam.tasks.business;

import android.content.Context;

import com.devmasterteam.tasks.constants.TaskConstants;
import com.devmasterteam.tasks.entities.APIResponse;
import com.devmasterteam.tasks.entities.TaskEntity;
import com.devmasterteam.tasks.infra.builder.URLBuilder;
import com.devmasterteam.tasks.entities.FullParameters;
import com.devmasterteam.tasks.infra.operation.OperationResult;
import com.devmasterteam.tasks.repository.api.ExternalRepository;
import com.devmasterteam.tasks.utils.FormatUrlParameters;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;

public class TaskBusiness extends BaseBusiness {

    private ExternalRepository mExternalRepository;

    /**
     * Construtor
     */
    public TaskBusiness(Context context) {
        super(context);
        this.mExternalRepository = new ExternalRepository(context);
    }

    /**
     * Faz a listagem de tarefas
     */
    public OperationResult<List<TaskEntity>> getList(int filter) {

        // Retorno
        OperationResult<List<TaskEntity>> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);

            if (filter == TaskConstants.TASKFILTER.NO_FILTER) {
                urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_GET_LIST_NO_FILTER);
            } else if (filter == TaskConstants.TASKFILTER.OVERDUE) {
                urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_GET_LIST_OVERDUE);
            } else {
                urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_GET_LIST_NEXT_7_DAYS);
            }

            // Adiciona cabeçalho
            AbstractMap<String, String> headerParameters = super.getHeaderParameters();

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.GET, urlBuilder.getUrl(), null, (HashMap) headerParameters);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {

                // Faz a conversão do json
                List<TaskEntity> list = new Gson().fromJson(httpResponse.json, new TypeToken<List<TaskEntity>>() {
                }.getType());

                // Sucesso
                operationResult.setResult(list);

            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

    /**
     * Obtém tarefa única
     */
    public OperationResult<TaskEntity> get(int taskId) {

        // Retorno
        OperationResult<TaskEntity> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);
            urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_GET_SPECIFIC);
            urlBuilder.addResource(String.valueOf(taskId));

            // Adiciona cabeçalho
            AbstractMap<String, String> headerParameters = super.getHeaderParameters();

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.GET, urlBuilder.getUrl(), null, (HashMap) headerParameters);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {

                // Faz a conversão do json
                TaskEntity taskEntity = new Gson().fromJson(httpResponse.json, TaskEntity.class);

                // Sucesso
                operationResult.setResult(taskEntity);

            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

    /**
     * Faz a inserção da tarefa
     */
    public OperationResult<Boolean> insert(TaskEntity taskEntity) {

        // Retorno
        OperationResult<Boolean> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);
            urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_INSERT);

            AbstractMap<String, String> parameters = new HashMap<>();
            parameters.put(TaskConstants.API_PARAMETER.DESCRIPTION, taskEntity.Description);
            parameters.put(TaskConstants.API_PARAMETER.PRIORITY_ID, String.valueOf(taskEntity.PriorityId));
            parameters.put(TaskConstants.API_PARAMETER.DUE_DATE, FormatUrlParameters.formatDate(taskEntity.DueDate));
            parameters.put(TaskConstants.API_PARAMETER.COMPLETE, FormatUrlParameters.formatBoolean(taskEntity.Complete));

            // Adiciona cabeçalho
            AbstractMap<String, String> headerParameters = super.getHeaderParameters();

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.POST, urlBuilder.getUrl(), (HashMap) parameters, (HashMap) headerParameters);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {
                operationResult.setResult(new Gson().fromJson(httpResponse.json, Boolean.class));
            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

    /**
     * Faz a atualização da tarefa
     */
    public OperationResult<Boolean> update(TaskEntity taskEntity) {

        // Retorno
        OperationResult<Boolean> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);
            urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_UPDATE);

            AbstractMap<String, String> parameters = new HashMap<>();
            parameters.put(TaskConstants.API_PARAMETER.ID, String.valueOf(taskEntity.Id));
            parameters.put(TaskConstants.API_PARAMETER.DESCRIPTION, taskEntity.Description);
            parameters.put(TaskConstants.API_PARAMETER.PRIORITY_ID, String.valueOf(taskEntity.PriorityId));
            parameters.put(TaskConstants.API_PARAMETER.DUE_DATE, FormatUrlParameters.formatDate(taskEntity.DueDate));
            parameters.put(TaskConstants.API_PARAMETER.COMPLETE, FormatUrlParameters.formatBoolean(taskEntity.Complete));

            // Adiciona cabeçalho
            AbstractMap<String, String> headerParameters = super.getHeaderParameters();

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.PUT, urlBuilder.getUrl(), (HashMap) parameters, (HashMap) headerParameters);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {
                operationResult.setResult(new Gson().fromJson(httpResponse.json, Boolean.class));
            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

    /**
     * Marca a tarefa como completa ou não
     */
    public OperationResult<Boolean> complete(int id, Boolean complete) {

        // Retorno
        OperationResult<Boolean> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);

            if (complete) {
                urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_COMPLETE);
            } else {
                urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_UNCOMPLETE);
            }

            AbstractMap<String, String> parameters = new HashMap<>();
            parameters.put(TaskConstants.API_PARAMETER.ID, String.valueOf(id));

            // Adiciona cabeçalho
            AbstractMap<String, String> headerParameters = super.getHeaderParameters();

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.PUT, urlBuilder.getUrl(), (HashMap) parameters, (HashMap) headerParameters);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {
                operationResult.setResult(new Gson().fromJson(httpResponse.json, Boolean.class));
            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

    /**
     * Faz a remoção de tarefa
     */
    public OperationResult<Boolean> delete(int taskId) {

        // Retorno
        OperationResult<Boolean> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);
            urlBuilder.addResource(TaskConstants.ENDPOINT.TASK_DELETE);

            AbstractMap<String, String> parameters = new HashMap<>();
            parameters.put(TaskConstants.API_PARAMETER.ID, String.valueOf(taskId));

            // Adiciona cabeçalho
            AbstractMap<String, String> headerParameters = super.getHeaderParameters();

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.DELETE, urlBuilder.getUrl(), (HashMap) parameters, (HashMap) headerParameters);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {
                operationResult.setResult(new Gson().fromJson(httpResponse.json, Boolean.class));
            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

}
