package com.synchronoss.querybuilder.model.kpi;

import org.elasticsearch.search.builder.SearchSourceBuilder;

public class KPIExecutionObject {

    /**
     *
     */
    private EsRepository esRepository;

    /**
     *
     */
    private SearchSourceBuilder priorSearchSourceBuilder;

    /**
     *
     */
    private SearchSourceBuilder currentSearchSourceBuilder;

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
     * Gets priorSearchSourceBuilder
     *
     * @return value of priorSearchSourceBuilder
     */
    public SearchSourceBuilder getPriorSearchSourceBuilder() {
        return priorSearchSourceBuilder;
    }

    /**
     * Sets priorSearchSourceBuilder
     */
    public void setPriorSearchSourceBuilder(SearchSourceBuilder priorSearchSourceBuilder) {
        this.priorSearchSourceBuilder = priorSearchSourceBuilder;
    }

    /**
     * Gets currentSearchSourceBuilder
     *
     * @return value of currentSearchSourceBuilder
     */
    public SearchSourceBuilder getCurrentSearchSourceBuilder() {
        return currentSearchSourceBuilder;
    }

    /**
     * Sets currentSearchSourceBuilder
     */
    public void setCurrentSearchSourceBuilder(SearchSourceBuilder currentSearchSourceBuilder) {
        this.currentSearchSourceBuilder = currentSearchSourceBuilder;
    }
}
