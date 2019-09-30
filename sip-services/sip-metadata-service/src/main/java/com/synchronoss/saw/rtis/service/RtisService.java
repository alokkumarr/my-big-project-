package com.synchronoss.saw.rtis.service;

import com.synchronoss.saw.rtis.model.request.RtisConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface RtisService {

  void createConfig(
      @NotNull(message = "Configuration definition cannot be null")
      @Valid RtisConfiguration configuration);

  Object fetchAppKeys(@NotNull(message = "Customer code cannot be null")
                      @Valid String customerCode);

  Object fetchConfigByAppKeys(@NotNull(message = "Application key cannot be null")
                              @Valid String appKey);

  Boolean deleteConfiguration(@NotNull(message = "Application key cannot be null")
                              @Valid String appKey);
}
