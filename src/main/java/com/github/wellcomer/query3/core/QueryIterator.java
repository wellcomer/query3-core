/*
 * Copyright (c) 2016. Sergey V. Katunin <sergey.blaster@gmail.com>
 * Licensed under the Apache License, Version 2.0
 */

package com.github.wellcomer.query3.core;

import java.util.Iterator;
import java.util.List;

/**
 * <h3>Итератор по заявкам.</h3>
 * Created on 17.11.15.
 */
public class QueryIterator implements Iterator<Query> {

    private Integer queryIterator;
    List<String> idList;
    QueryStorage stgBackend;

    public QueryIterator(QueryStorage stgBackend, long modifiedSince){
        this.stgBackend = stgBackend;
        try {
            idList = stgBackend.idList(modifiedSince);
        }
        catch (Exception e){
            e.printStackTrace();
            this.stgBackend = null;
        }
        queryIterator = 0;
    }

    @Override
    public boolean hasNext() {
        return !(stgBackend == null || queryIterator >= idList.size());
    }

    @Override
    public Query next() {
        try {
            String queryID = idList.get(queryIterator++);
            return stgBackend.get(queryID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
