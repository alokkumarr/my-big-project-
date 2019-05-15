package com.synchronoss.saw.analysis.service;

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

  Analysis getAnalysis(
      @NotNull(message = "AnalysisID cannot be null") @NotNull String analysisID,
      @Valid Ticket ticket);


  List<Analysis> getAnalysisByCategory(
      @NotNull(message = "AnalysisID cannot be null") @NotNull String categoryID,
      @Valid Ticket ticket);

  List<Analysis> getAnalysisByCategoryForUserId(
      @NotNull(message = "categoryID cannot be null") @NotNull String categoryID,
      @NotNull(message = "userId cannot be null") @NotNull Long userId,
      @Valid Ticket ticket);
}
