package com.github.valdar.iat.services;

/**
 * Created by valdar on 22/01/16.
 */
public interface GridService {

    String getById( String id );
    void putById( String id, String key );

}
