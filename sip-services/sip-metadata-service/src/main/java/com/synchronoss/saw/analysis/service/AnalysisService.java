package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.analysis.modal.Analysis;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface AnalysisService {

  Analysis createAnalysis(
      @NotNull(message = "Analysis definition cannot be null") @Valid Analysis analysis,
      @Valid Ticket ticket);

  Analysis updateAnalysis(
      @NotNull(message = "Analysis definition cannot be null") @Valid Analysis analysis,
      @Valid Ticket ticket);

  void deleteAnalysis(
      @NotNull(message = "AnalysisID cannot be null") @NotNull String analysisID,
      @Valid Ticket ticket);

  ObjectNode getAnalysis(
      @NotNull(message = "AnalysisID cannot be null") @NotNull String analysisID,
      @Valid Ticket ticket);

  List<ObjectNode> getAnalysisByCategory(
      @NotNull(message = "categoryID cannot be null") @NotNull String categoryID,
      @Valid Ticket ticket);

  List<ObjectNode> getAnalysisByCategoryForUserId(
      @NotNull(message = "categoryID cannot be null") @NotNull String categoryID,
      @NotNull(message = "userId cannot be null") @NotNull Long userId,
      @Valid Ticket ticket);
}
