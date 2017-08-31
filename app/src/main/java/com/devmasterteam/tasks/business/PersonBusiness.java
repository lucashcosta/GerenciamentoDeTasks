package com.devmasterteam.tasks.business;

import android.content.Context;

import com.devmasterteam.tasks.constants.TaskConstants;
import com.devmasterteam.tasks.entities.HeaderEntity;
import com.devmasterteam.tasks.entities.APIResponse;
import com.devmasterteam.tasks.infra.builder.URLBuilder;
import com.devmasterteam.tasks.entities.FullParameters;
import com.devmasterteam.tasks.infra.operation.OperationResult;
import com.devmasterteam.tasks.infra.security.SecurityPreferences;
import com.devmasterteam.tasks.repository.api.ExternalRepository;
import com.devmasterteam.tasks.utils.FormatUrlParameters;
import com.google.gson.Gson;

import java.util.AbstractMap;
import java.util.HashMap;

public class PersonBusiness extends BaseBusiness {

    private ExternalRepository mExternalRepository;
    private Context mContext;

    /**
     * Construtor
     */
    public PersonBusiness(Context context) {
        super(context);
        this.mContext = context;
        this.mExternalRepository = new ExternalRepository(context);
    }

    /**
     * Faz a criação do usuário
     */
    public OperationResult<Boolean> create(String email, String password, String name) {

        // Retorno
        OperationResult<Boolean> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);
            urlBuilder.addResource(TaskConstants.ENDPOINT.AUTHENTICATION_CREATE);

            // Adiciona parâmetros para requisição
            AbstractMap<String, String> parameters = new HashMap<>();
            parameters.put(TaskConstants.API_PARAMETER.EMAIL, email);
            parameters.put(TaskConstants.API_PARAMETER.PASSWORD, password);
            parameters.put(TaskConstants.API_PARAMETER.NAME, name);
            parameters.put(TaskConstants.API_PARAMETER.RECEIVE_NEWS, FormatUrlParameters.formatBoolean(true));

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.POST, urlBuilder.getUrl(), (HashMap) parameters, null);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {

                // Faz a conversão do json
                HeaderEntity headerEntity = new Gson().fromJson(httpResponse.json, HeaderEntity.class);

                // Armazena dados da 'session' no SharedPreferences
                SecurityPreferences preferences = new SecurityPreferences(this.mContext);
                preferences.storeString(TaskConstants.HEADER.PERSON_KEY, headerEntity.personKey);
                preferences.storeString(TaskConstants.HEADER.TOKEY_KEY, headerEntity.token);
                preferences.storeString(TaskConstants.USER.NAME, headerEntity.name);
                preferences.storeString(TaskConstants.USER.EMAIL, email);

                // Sucesso
                operationResult.setResult(true);
            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

    /**
     * Faz login
     */
    public OperationResult<Boolean> login(String email, String password) {

        // Retorno
        OperationResult<Boolean> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);
            urlBuilder.addResource(TaskConstants.ENDPOINT.AUTHENTICATION_LOGIN);

            // Adiciona parâmetros para requisição
            AbstractMap<String, String> parameters = new HashMap<>();
            parameters.put(TaskConstants.API_PARAMETER.EMAIL, email);
            parameters.put(TaskConstants.API_PARAMETER.PASSWORD, password);

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.POST, urlBuilder.getUrl(), (HashMap) parameters, null);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {

                // Faz a conversão do json
                HeaderEntity headerEntity = new Gson().fromJson(httpResponse.json, HeaderEntity.class);

                // Armazena dados da 'session' no SharedPreferences
                SecurityPreferences preferences = new SecurityPreferences(this.mContext);
                preferences.storeString(TaskConstants.HEADER.PERSON_KEY, headerEntity.personKey);
                preferences.storeString(TaskConstants.HEADER.TOKEY_KEY, headerEntity.token);
                preferences.storeString(TaskConstants.USER.NAME, headerEntity.name);
                preferences.storeString(TaskConstants.USER.EMAIL, email);

                // Sucesso
                operationResult.setResult(true);

            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

}
