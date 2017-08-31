package com.devmasterteam.tasks.infra.builder;

public class URLBuilder {

    // URL principal para chamda da API
    private String mServiceUrl;

    /**
     * Construtor
     */
    public URLBuilder(String mainUrl) {
        this.mServiceUrl = mainUrl;
    }

    /**
     * Adiciona o recurso
     */
    public void addResource(String resource) {

        // Certifica de que não existem múltiplas // na URL
        while (resource.endsWith("/"))
            resource = resource.substring(0, resource.length() - 1);

        // Adiciona na URL principal
        this.mServiceUrl = this.mServiceUrl.concat("/" + resource + "/");
    }

    /**
     * Retorna URL
     */
    public String getUrl() {
        return this.mServiceUrl;
    }

}
