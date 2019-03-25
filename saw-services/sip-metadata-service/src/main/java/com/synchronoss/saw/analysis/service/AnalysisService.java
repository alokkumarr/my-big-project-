package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.modal.Ticket;

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

  Analysis getAnalysis(
      @NotNull(message = "AnalysisID cannot be null") @NotNull String analysisID,
      @Valid Ticket ticket);

  List<ObjectNode> getAnalysisByCategory(
      @NotNull(message = "AnalysisID cannot be null") @NotNull String categoryID,
      @Valid Ticket ticket);
}
