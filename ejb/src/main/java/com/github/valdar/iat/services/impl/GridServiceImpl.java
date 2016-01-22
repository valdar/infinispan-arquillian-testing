package com.github.valdar.iat.services.impl;

import com.github.valdar.iat.services.GridService;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoSchemaBuilderException;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by valdar on 22/01/16.
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class GridServiceImpl implements GridService {

    private static final String serverList = System.getProperty("JDG_SERVERLIST") !=null
            ? System.getProperty("JDG_SERVERLIST")
            : "127.0.0.1:11222";
    private static final boolean sslEnabled = System.getProperty("JDG_SSL_ENABLED")!=null
            && "true".equals(System.getProperty("JDG_SSL_ENABLED"))
            ? true : false;
    private static final String keyStoreName = System.getProperty("JDG_SSL_KEYSTORE")!=null
            ? System.getProperty("JDG_SSL_KEYSTORE")
            : "keystore.jks";
    private static final String keyStorePassword = System.getProperty("JDG_SSL_KEYSTORE_PWD")!=null
            ? System.getProperty("JDG_SSL_KEYSTORE_PWD")
            : "changeit";
    private static final String trustStoreName = System.getProperty("JDG_SSL_TRUSTSTORE")!=null
            ? System.getProperty("JDG_SSL_TRUSTSTORE")
            : "keystore.jks";
    private static final String trustStorePassword = System.getProperty("JDG_SSL_TRUSTSTORE_PWD")!=null
            ? System.getProperty("JDG_SSL_TRUSTSTORE_PWD")
            : "changeit";

    private static final String protocolVersion = System.getProperty("JDG_PROTOCAL_VERSION")!=null
            ? System.getProperty("JDG_PROTOCAL_VERSION")
            : "2.2";
    private static final String searchDealCacheName = System.getProperty("SEARCHDEAL_CACHE_NAME")!=null
            ? System.getProperty("SEARCHDEAL_CACHE_NAME")
            : "deals-search";

    @Inject
    private Logger LOG;

    private RemoteCacheManager remoteCacheManager;

    @PostConstruct
    private void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("JDG CacheManagerService init() - start");
        }

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServers(serverList).marshaller(new ProtoStreamMarshaller());
        builder.protocolVersion(protocolVersion);
        if(sslEnabled){
            builder.security()
                    .ssl()
                    .enable()
                    .keyStoreFileName(tccl.getResource(keyStoreName).getPath())
                    .keyStorePassword(keyStorePassword.toCharArray())
                    .trustStoreFileName(tccl.getResource(trustStoreName).getPath())
                    .trustStorePassword(trustStorePassword.toCharArray());
        }

        remoteCacheManager = new RemoteCacheManager(builder.build());

//        SerializationContext serCtx = ProtoStreamMarshaller
//                .getSerializationContext(remoteCacheManager);
//        configureProtobufMarshaller(serCtx);

        remoteCacheManager.start();

        if (LOG.isDebugEnabled()) {
            LOG.debug("JDG CacheManagerService init() - end");
        }
    }

//    private void configureProtobufMarshaller(SerializationContext serCtx) {
//        // generate and register a Protobuf schema and marshallers based
//        // on annotated classes
//        ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
//        String generatedSchema;
//        try {
//            // generate the 'twb.proto' schema file based on the annotations on
//            // passed classes and register it with the SerializationContext of the
//            // client
//            generatedSchema = protoSchemaBuilder.fileName("twb.proto")
//                    .packageName("com.bp.ist.dps.common.model")
//                    .addClass(Deal.class)
//                    .build(serCtx);
//
//            // register the schemas with the server too
//            RemoteCache<String, String> metadataCache = remoteCacheManager
//                    .getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
//            metadataCache.put("twb.proto", generatedSchema);
//
//            String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
//            if (errors != null) {
//                throw new IllegalStateException("Some Protobuf schema files contain errors:\n"+ errors);
//            }
//
//        } catch (ProtoSchemaBuilderException | IOException e) {
//            throw new IllegalStateException( "An error occurred initializing HotRod protobuf marshaller:", e);
//        }
//    }

    @PreDestroy
    private void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("JDG CacheManagerService destroy() - start");
        }

        remoteCacheManager.stop();

        if (LOG.isDebugEnabled()) {
            LOG.debug("JDG CacheManagerService destroy() - end");
        }
    }

    @Override
    public String getById(String id) {
        RemoteCache<String,String> cache = remoteCacheManager.getCache(searchDealCacheName);
        return cache.get(id);
    }

    @Override
    public void putById(String id, String key) {
        RemoteCache<String,String> cache = remoteCacheManager.getCache(searchDealCacheName);
        cache.put(id,key);
    }
}
