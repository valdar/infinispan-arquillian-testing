package com.github.valdar.iat.rest.producers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;


@ApplicationScoped
public class Slf4jProducers {

    @Produces
    public Logger produce(InjectionPoint ip) {
        return LoggerFactory.getLogger(ip.getMember().getDeclaringClass()
                .getName());
    }
}
