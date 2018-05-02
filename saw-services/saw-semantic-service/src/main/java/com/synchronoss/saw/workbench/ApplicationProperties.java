package com.synchronoss.saw.workbench;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = ApplicationProperties.CONFIGURATION_PROPERTY_PREFIX,ignoreUnknownFields = false)
@Component
public class ApplicationProperties {

    static final String CONFIGURATION_PROPERTY_PREFIX = "application";
    private final Async async = new Async();

    public Async getAsync() {
        return async;
    }

    public static class Async {

        private Integer corePoolSize;
        private Integer maxPoolSize;
        private Integer queueCapacity;

        public Integer getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(final Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(final Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public Integer getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(final Integer queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }
}
