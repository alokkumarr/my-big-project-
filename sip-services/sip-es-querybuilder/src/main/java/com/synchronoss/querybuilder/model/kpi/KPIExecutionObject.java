package com.synchronoss.querybuilder.model.kpi;

import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;

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

    private List<DataField> dataFields;

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

    /**
     * Gets dataFields
     *
     * @return value of dataFields
     */
    public List<DataField> getDataFields() {
        return dataFields;
    }

    /**
     * Sets dataFields
     */
    public void setDataFields(List<DataField> dataFields) {
        this.dataFields = dataFields;
    }
}
