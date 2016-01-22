package com.github.valdar.iat.rest;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class TestUtils {

    public static WebArchive createFullDeployment(){

        WebArchive webArchive = ShrinkWrap.create(WebArchive.class)
                .addPackage("com.github.valdar.iat.rest")
                .addPackage("com.github.valdar.iat.rest.endpoints")
                .addPackage("com.github.valdar.iat.rest.endpoints.impl")
                .addPackage("com.github.valdar.iat.services")
                .addPackage("com.github.valdar.iat.rest.mock")
                .addPackage("com.github.valdar.iat.rest.producers")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return webArchive;
    }

}
