package com.github.mwierzchowski.helios.adapter.jpa;

import com.github.mwierzchowski.helios.HeliosProperties;
import com.p6spy.engine.spy.P6DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Intercepts SQL queries. It uses P6Spy under the hood. Queries are logged by {@link SqlLogger}.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "helios.intercept-sql")
public class SqlInterceptor implements BeanPostProcessor {
    /**
     * Application properties
     */
    private final HeliosProperties heliosProperties;

    /**
     * Configures P6Spy for intercepting SQLs.
     */
    @PostConstruct
    public void setup() {
        System.getProperties().put("p6spy.config.appender", SqlLogger.class.getCanonicalName());
        System.getProperties().put("p6spy.config.databaseDialectDateFormat", heliosProperties.getTimestampFormat());
    }

    /**
     * Decorates all data sources with {@link P6DataSource}.
     * @param bean bean to potentially decorate
     * @param beanName bean's name
     * @return potentially decorated bean
     * @throws BeansException in case of error
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            log.warn("Using P6Spy to intercept {} SQLs may impact performance and security", beanName);
            return new P6DataSource((DataSource) bean);
        }
        return bean;
    }
}