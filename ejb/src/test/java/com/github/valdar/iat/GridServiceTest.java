package com.github.valdar.iat;

import com.github.valdar.iat.services.GridService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class GridServiceTest {
    @Deployment( name = "dep") @TargetsContainer("as")
    public static Archive<?> createDeployment() {
        Archive<?> archive = TestUtils.createFullDeployment();
        return archive;
    }

    @Inject
    private Logger LOG;

    @Inject
    private GridService gridService;

    @Test
    public void testTrySlowSearchServiceWithGridInitialised() {
        gridService.putById("testKEY","testValue");
        String result = gridService.getById("testKEY");
        assertEquals("testValue", result);
    }
}