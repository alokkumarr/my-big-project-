package com.synchronoss.querybuilder.model.globalfilter;

import org.elasticsearch.search.builder.SearchSourceBuilder;

public class GlobalFilterExecutionObject {

    /**
     *
     */
    private EsRepository esRepository;

    /**
     *
     */
    private SearchSourceBuilder searchSourceBuilder;

    /**
     * Gets esRepository
     *
     * @return value of esRepository
     */
    public EsRepository getEsRepository() {
        return esRepository;
    }

    /**
     * Sets esRepository
     */
    public void setEsRepository(EsRepository esRepository) {
        this.esRepository = esRepository;
    }

    /**
     * Gets searchSourceBuilder
     *
     * @return value of searchSourceBuilder
     */
    public SearchSourceBuilder getSearchSourceBuilder() {
        return searchSourceBuilder;
    }

    /**
     * Sets searchSourceBuilder
     */
    public void setSearchSourceBuilder(SearchSourceBuilder searchSourceBuilder) {
        this.searchSourceBuilder = searchSourceBuilder;
    }
}
