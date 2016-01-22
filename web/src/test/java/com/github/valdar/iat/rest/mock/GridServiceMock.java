package com.github.valdar.iat.rest.mock;

import com.github.valdar.iat.services.GridService;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;

/**
 * Created by valdar on 22/01/16.
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class GridServiceMock implements GridService {

    private static final HashMap<String,String> cache = new HashMap<String,String>();

    @Override
    public String getById(String id) {
        return cache.get(id);
    }

    @Override
    public void putById(String id, String key) {
        cache.put(id,key);
    }
}
