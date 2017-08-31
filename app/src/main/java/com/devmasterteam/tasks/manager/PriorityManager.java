package com.devmasterteam.tasks.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.devmasterteam.tasks.business.PriorityBusiness;
import com.devmasterteam.tasks.entities.PriorityEntity;
import com.devmasterteam.tasks.infra.operation.OperationListener;
import com.devmasterteam.tasks.infra.operation.OperationResult;

import java.util.List;

public class PriorityManager {

    private PriorityBusiness mPriorityBusiness;

    /**
     * Construtor
     */
    public PriorityManager(Context context) {
        this.mPriorityBusiness = new PriorityBusiness(context);
    }

    public List<PriorityEntity> getListLocal() {
        return this.mPriorityBusiness.getListLocal();
    }

    /**
     * Faz a listagem de prioridades
     */
    public void getList(final OperationListener<List<PriorityEntity>> listener) {
        AsyncTask<Void, Integer, OperationResult<List<PriorityEntity>>> task = new AsyncTask<Void, Integer, OperationResult<List<PriorityEntity>>>() {
            @Override
            protected OperationResult<List<PriorityEntity>> doInBackground(Void... voids) {
                return mPriorityBusiness.getList();
            }

            @Override
            protected void onPostExecute(OperationResult<List<PriorityEntity>> result) {
                int error = result.getError();
                if (error != OperationResult.NO_ERROR) {
                    listener.onError(error, result.getErrorMessage());
                } else {
                    listener.onSuccess(result.getResult());
                }
            }
        };

        // Executa async
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
