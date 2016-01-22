package com.github.valdar.iat;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

public class TestUtils {

    private final static PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");

    private static JavaArchive getJar(){
        return ShrinkWrap.create(JavaArchive.class, "ejb.jar")
                .addPackage("com.github.valdar.iat")
                .addPackage("com.github.valdar.iat.services")
                .addPackage("com.github.valdar.iat.services.impl")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public static EnterpriseArchive createFullDeployment(){
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "iat.ear");

        ear.addAsModule(getJar())
                .setApplicationXML("test-application.xml")
                .addAsLibraries(resolver.resolve("org.infinispan:infinispan-core").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.infinispan:infinispan-commons").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.infinispan:infinispan-client-hotrod").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.infinispan:infinispan-query-dsl").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.infinispan:infinispan-remote-query-client").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("com.google.guava:guava").withTransitivity().asFile());
        return ear;
    }

}
