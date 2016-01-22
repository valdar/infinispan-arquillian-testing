package com.github.valdar.iat.rest;

import com.github.valdar.iat.rest.endpoints.KeyValue;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class SaveKeyValueRestTest {

    @ArquillianResource
    private URL deploymentURL;

    @Deployment(testable = false)
    public static WebArchive create() {
        WebArchive webArchive = TestUtils.createFullDeployment();
        return webArchive;
    }

    @Test
    public void testSearch(@ArquillianResteasyResource("rest") KeyValue skvs)
    {

//        try {
//            Thread.sleep(10000000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        skvs.putById("testKEY","testVALUE");
        String result = skvs.getById("testKEY");

        assertEquals("testVALUE", result);
    }

}
