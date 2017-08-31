package com.devmasterteam.tasks.repository.api;

import android.content.Context;

import com.devmasterteam.tasks.constants.TaskConstants;
import com.devmasterteam.tasks.entities.APIResponse;
import com.devmasterteam.tasks.infra.exception.InternetNotAvailableException;
import com.devmasterteam.tasks.entities.FullParameters;
import com.devmasterteam.tasks.utils.NetworkUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;

public class ExternalRepository {

    // Contexto da activity
    private Context mContext;

    // Construtor
    public ExternalRepository(Context context) {
        this.mContext = context;
    }

    /**
     * Executa chamada a API
     */
    public APIResponse execute(FullParameters fullParameters) throws InternetNotAvailableException {

        // Verifica se está conectado na internet
        if (!NetworkUtils.isConnectionAvailable(this.mContext)) {
            throw new InternetNotAvailableException();
        }

        InputStream inputStream;
        APIResponse response;
        HttpURLConnection conn;

        try {

            URL url;

            if (fullParameters.method == TaskConstants.OPERATION_METHOD.GET)
                url = new URL(fullParameters.url + getQuery(fullParameters.parameters, fullParameters.method));
            else
                url = new URL(fullParameters.url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(150000);
            conn.setRequestMethod(fullParameters.method);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);

            // Faz tratamento para headers
            if (fullParameters.headerParameters != null) {
                Iterator it = fullParameters.headerParameters.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    conn.setRequestProperty(pair.getKey().toString(), pair.getValue().toString());
                    it.remove();
                }
            }

            // Tratamento para os verbos que mandam dados no corpo
            if (!fullParameters.method.equals(TaskConstants.OPERATION_METHOD.GET)) {

                String query = getQuery(fullParameters.parameters, fullParameters.method);
                byte[] postDataBytes = query.getBytes("UTF-8");
                int postDataLength = postDataBytes.length;

                // Faz tratamento para parâmetros de body
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);
            }

            // Starts the query
            conn.connect();

            // Caso seja erro 404, não existe input strem fazendo com que caia sempre na exception
            if (conn.getResponseCode() != TaskConstants.STATUS_CODE.SUCCESS) {

                // Lê conteúdo
                inputStream = conn.getErrorStream();
                response = new APIResponse(getStringFromInputStream(inputStream), conn.getResponseCode());

            } else {

                // Lê conteúdo
                inputStream = conn.getInputStream();

                // Monta a classe de resposta da API
                response = new APIResponse(getStringFromInputStream(inputStream), conn.getResponseCode());
            }

            inputStream.close();
            conn.disconnect();

        } catch (Exception e) {
            response = new APIResponse("", TaskConstants.STATUS_CODE.NOT_FOUND);
        }

        // Retorna entidade preenchida
        return response;

    }

    /**
     * Faz a conversão do retorno do webservice para string
     */
    private String getStringFromInputStream(InputStream is) {

        if (is == null)
            return "";

        BufferedReader br;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();

        } catch (IOException e) {
            return "";
        }

        return sb.toString();
    }

    /**
     * Monta a query string
     */
    private String getQuery(AbstractMap<String, String> params, String method) throws UnsupportedEncodingException {
        if (params == null)
            return "";

        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> e : params.entrySet()) {
            if (first) {
                if (method.equals(TaskConstants.OPERATION_METHOD.GET)) {
                    result.append("?");
                }
                first = false;
            } else
                result.append("&");

            result.append(URLEncoder.encode(e.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(e.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
