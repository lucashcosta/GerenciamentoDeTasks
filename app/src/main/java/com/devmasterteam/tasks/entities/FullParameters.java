package com.devmasterteam.tasks.entities;

import java.util.AbstractMap;
import java.util.HashMap;

public class FullParameters {

    // Método HTTP
    public String method;

    // URL com recurso que será chamado
    public String url;

    // Parametros
    public AbstractMap<String, String> parameters;

    // Parâmetros header
    public AbstractMap<String, String> headerParameters;

    // Construtor
    public FullParameters(String method, String url, HashMap params, HashMap headerParams) {
        this.method = method;
        this.url = url;
        this.parameters = params;
        this.headerParameters = headerParams;
    }

}
