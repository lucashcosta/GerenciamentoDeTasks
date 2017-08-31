package com.devmasterteam.tasks.business;

import android.content.Context;

import com.devmasterteam.tasks.constants.PriorityCacheConstants;
import com.devmasterteam.tasks.constants.TaskConstants;
import com.devmasterteam.tasks.entities.APIResponse;
import com.devmasterteam.tasks.entities.PriorityEntity;
import com.devmasterteam.tasks.infra.builder.URLBuilder;
import com.devmasterteam.tasks.entities.FullParameters;
import com.devmasterteam.tasks.infra.operation.OperationResult;
import com.devmasterteam.tasks.repository.api.ExternalRepository;
import com.devmasterteam.tasks.repository.local.PriorityRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;

public class PriorityBusiness extends BaseBusiness {

    private ExternalRepository mExternalRepository;
    private PriorityRepository mPriorityRepository;

    public PriorityBusiness(Context context) {
        super(context);
        this.mExternalRepository = new ExternalRepository(context);
        this.mPriorityRepository = PriorityRepository.getInstance(context);
    }

    /**
     * Faz a listagem de prioridade
     */
    public OperationResult<List<PriorityEntity>> getList() {

        // Retorno
        OperationResult<List<PriorityEntity>> operationResult = new OperationResult<>();

        try {

            // Monta query
            URLBuilder urlBuilder = new URLBuilder(TaskConstants.ENDPOINT.ROOT);
            urlBuilder.addResource(TaskConstants.ENDPOINT.PRIORITY_GET);

            // Adiciona cabeçalho
            AbstractMap<String, String> headerParameters = super.getHeaderParameters();

            // Cria objeto de requisição com parâmetros
            FullParameters fullParameters = new FullParameters(TaskConstants.OPERATION_METHOD.GET, urlBuilder.getUrl(), null, (HashMap) headerParameters);

            // Executa
            APIResponse httpResponse = this.mExternalRepository.execute(fullParameters);

            // Sucesso
            if (httpResponse.statusCode == TaskConstants.STATUS_CODE.SUCCESS) {

                // Faz a conversão do json
                List<PriorityEntity> list = new Gson().fromJson(httpResponse.json, new TypeToken<List<PriorityEntity>>() {
                }.getType());
                operationResult.setResult(list);

                // 1 - Atualiza o cache
                PriorityCacheConstants.setCache(list);

                // 2 - Limpa o banco de dados
                this.clear();

                // 3 - Atualiza as informações
                this.insert(list);

            } else {
                operationResult.setError(super.handleResponseCode(httpResponse.statusCode), super.getErrorMessage(httpResponse.json));
            }
        } catch (Exception e) {
            operationResult.setError(super.handleExceptionCode(e), super.handleExceptionMessage(e));
        }

        return operationResult;
    }

    /**
     * Carrega todos os prioridades
     */
    public List<PriorityEntity> getListLocal() {
        return this.mPriorityRepository.getList();
    }

    /**
     * Limpa todos os registros do banco
     */
    public void clear() {
        this.mPriorityRepository.clear();
    }

    /**
     * Faz inserção em massa de prioridades
     */
    public void insert(List<PriorityEntity> list) {
        this.mPriorityRepository.insert(list);
    }

}
