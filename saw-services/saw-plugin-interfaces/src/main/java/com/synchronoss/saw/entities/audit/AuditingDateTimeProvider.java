package com.synchronoss.saw.entities.audit;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.data.auditing.DateTimeProvider;

public class AuditingDateTimeProvider implements DateTimeProvider {

  private final DateTimeService dateTimeService;

  public AuditingDateTimeProvider(DateTimeService dateTimeService) {
    this.dateTimeService = dateTimeService;
  }

  @Override
  public Optional<TemporalAccessor> getNow() {
    return Optional.of(dateTimeService.getCurrentDateAndTime());
  }
}
