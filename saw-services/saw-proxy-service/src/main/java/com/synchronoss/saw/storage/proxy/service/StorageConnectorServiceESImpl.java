package com.synchronoss.saw.storage.proxy.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.validation.constraints.NotNull;

import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequest;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;

@Service
public class StorageConnectorServiceESImpl implements StorageConnectorService {

  @Value("${elastic-xpack.cluster-active}")
  @NotNull
  private Boolean active;

  @Value("${elastic-xpack.clsuter-username}")
  private String username;

  @Value("${elastic-xpack.cluster-password}")
  private Boolean password;

  @Value("${elastic-search.cluster-name}")
  @NotNull
  private String clusterName;

  @Value("${elastic-search.transport-hosts}")
  @NotNull
  private String[] hosts;

  @Value("${elastic-search.transport-port}")
  @NotNull
  private String port;
 
  public SearchResponse searchDocuments(String query, StorageProxy proxyDetails)
      throws JsonProcessingException, IOException {
    Preconditions.checkArgument(query!=null && !"".equals(query), "query cannnot be null.");
    SearchResponse response = null;
    TransportClient client = null;
    try {
      if (active){
        PreBuiltTransportClient preBuiltTransportClientWithXPACK = new PreBuiltTransportClient(
            Settings.builder().put("client.transport.nodes_sampler_interval", "5s")
                .put("client.transport.sniff", false).put("transport.tcp.compress", true)
                .put("cluster.name", "sncr-salesdemo")
                .put("xpack.security.transport.ssl.enabled", false)
                .put("request.headers.X-Found-Cluster", clusterName)
                .put("xpack.security.user", username + ":" + password).build());
        client = preBuiltTransportClientWithXPACK.addTransportAddresses(prepareHostAddresses(hosts));
        response = client.prepareSearch(proxyDetails.getIndexName()).setTypes(proxyDetails.getObjectType())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .setSource(query).get();
        preBuiltTransportClientWithXPACK.close();
      } else {
        PreBuiltTransportClient preBuiltTransportClientNoXPACK = new PreBuiltTransportClient(
            Settings.builder().put("client.transport.nodes_sampler_interval", "5s")
                .put("client.transport.sniff", false).put("transport.tcp.compress", true)
                .put("cluster.name", clusterName)
                .put("request.headers.X-Found-Cluster", clusterName).build());
        client = preBuiltTransportClientNoXPACK
            .addTransportAddresses(prepareHostAddresses(hosts));
        response = client.prepareSearch(proxyDetails.getIndexName()).setTypes(proxyDetails.getObjectType())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(proxyDetails.getPageNum()).setSize(proxyDetails.getPageSize())
            .setSource(query).get();
        preBuiltTransportClientNoXPACK.close();
      }
      client.close();
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return response;
  }

  @Override
  public ValidateQueryResponse validateQuery(String query, StorageProxy proxyDetails) throws Exception {
    Preconditions.checkArgument(query!=null && !"".equals(query), "query cannnot be null.");
    final ValidateQueryRequest request = new ValidateQueryRequest();
    TransportClient client = null;
    ValidateQueryResponse response =null;
    if (query !=null & !query.equals("")){
      request.source(query);
      request.indices(proxyDetails.getIndexName());
      try {
        if (active){
          PreBuiltTransportClient preBuiltTransportClientWithXPACK = new PreBuiltTransportClient(
              Settings.builder().put("client.transport.nodes_sampler_interval", "5s")
                  .put("client.transport.sniff", false).put("transport.tcp.compress", true)
                  .put("cluster.name", "sncr-salesdemo")
                  .put("xpack.security.transport.ssl.enabled", false)
                  .put("request.headers.X-Found-Cluster", clusterName)
                  .put("xpack.security.user", username + ":" + password).build());
          client = preBuiltTransportClientWithXPACK.addTransportAddresses(prepareHostAddresses(hosts));
          response = client.admin().indices().validateQuery(request).actionGet();
          preBuiltTransportClientWithXPACK.close();
        } else {
          PreBuiltTransportClient preBuiltTransportClientNoXPACK = new PreBuiltTransportClient(
              Settings.builder().put("client.transport.nodes_sampler_interval", "5s")
                  .put("client.transport.sniff", false).put("transport.tcp.compress", true)
                  .put("cluster.name", clusterName)
                  .put("request.headers.X-Found-Cluster", clusterName).build());
          client = preBuiltTransportClientNoXPACK
              .addTransportAddresses(prepareHostAddresses(hosts));
          response = client.admin().indices().validateQuery(request).actionGet();
          preBuiltTransportClientNoXPACK.close();
        }
        client.close();
      } finally {
        if (client != null) {
          client.close();
        }
      }
      
    }
    return response;
  }
  
  @Override
  public UpdateResponse updateDocument(String id, StorageProxy proxyDetails) throws Exception {
    Preconditions.checkArgument(id!=null && !"".equals(id), "query cannnot be null.");
    UpdateResponse updateResponse = null;
    TransportClient client = null;
    try {
      client = prepareESConnection();
      if (proxyDetails.getQuery()!=null && !"".equals(proxyDetails.getQuery())){
        updateResponse = client.prepareUpdate(proxyDetails.getIndexName(), proxyDetails.getObjectType(), proxyDetails.getEntityId()).
            setDoc(proxyDetails.getQuery()).get();
      }
      else{
        updateResponse = client.prepareUpdate(proxyDetails.getIndexName(), proxyDetails.getObjectType(), proxyDetails.getEntityId()).get();
      }
    } finally {
      client.close();
    }
    return updateResponse;
  }

  @Override
  public DeleteResponse deleteDocument(String id, StorageProxy proxyDetails) throws Exception {
    Preconditions.checkArgument(id!=null && !"".equals(id), "id cannnot be null for a delete operation.");
    TransportClient client = null;
    DeleteResponse deleteResponse = null;
    try {
      client = prepareESConnection();
      deleteResponse = client.prepareDelete(proxyDetails.getIndexName(), proxyDetails.getObjectType(), proxyDetails.getEntityId()).get();
    } finally {
      client.close();
    }
    return deleteResponse;
  }

  @Override
  public IndexResponse indexDocument(String query, StorageProxy proxyDetails) throws Exception {
    Preconditions.checkArgument(query != null && !"".equals(query), "query cannnot be null.");
    TransportClient client = null;
    IndexResponse indexResponse = null;
    try {
      client = prepareESConnection();
      indexResponse = client.prepareIndex(proxyDetails.getIndexName(), proxyDetails.getObjectType()).setSource(query).get();
    } finally {
      client.close();
    }
    return indexResponse;
  }
  
  @Override
  public Long countDocument(String query, StorageProxy proxyDetails) throws Exception {
    Preconditions.checkArgument(query!=null && !"".equals(query), "query cannnot be null.");
    SearchResponse searchResponse = null;
    TransportClient client = null;
    try {
      client = prepareESConnection();
      searchResponse = client.prepareSearch(proxyDetails.getIndexName(), proxyDetails.getObjectType()).setSource(query).get();
    } finally {
      client.close();
    }
    return searchResponse.getHits().getTotalHits();
  }
private TransportAddress[] prepareHostAddresses(String[] hosts) throws NumberFormatException, UnknownHostException
  {
    TransportAddress [] transports = new TransportAddress[hosts.length];
    TransportAddress transport = null;
    for (int i=0; i<hosts.length;i++){
      transport = new InetSocketTransportAddress(InetAddress.getByName(hosts[i]), Integer.parseInt(port)); 
      transports[i] = transport;
    } 
    return transports;
  }

  private TransportClient prepareESConnection() throws Exception {
    TransportClient client = null;
      if (active){
        PreBuiltTransportClient preBuiltTransportClientWithXPACK = new PreBuiltTransportClient(
            Settings.builder().put("client.transport.nodes_sampler_interval", "5s")
                .put("client.transport.sniff", false).put("transport.tcp.compress", true)
                .put("cluster.name", "sncr-salesdemo")
                .put("xpack.security.transport.ssl.enabled", false)
                .put("request.headers.X-Found-Cluster", clusterName)
                .put("xpack.security.user", username + ":" + password).build());
        client = preBuiltTransportClientWithXPACK.addTransportAddresses(prepareHostAddresses(hosts));
        preBuiltTransportClientWithXPACK.close();
        
      } else {
        PreBuiltTransportClient preBuiltTransportClientNoXPACK = new PreBuiltTransportClient(
            Settings.builder().put("client.transport.nodes_sampler_interval", "5s")
                .put("client.transport.sniff", false).put("transport.tcp.compress", true)
                .put("cluster.name", clusterName)
                .put("request.headers.X-Found-Cluster", clusterName).build());
        client = preBuiltTransportClientNoXPACK
            .addTransportAddresses(prepareHostAddresses(hosts));
        preBuiltTransportClientNoXPACK.close();
      }
      return client;
    
  }

}
