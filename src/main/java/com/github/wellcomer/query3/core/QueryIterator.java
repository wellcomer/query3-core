package com.github.wellcomer.query3.core;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * <h3>Итератор по заявкам.</h3>
 * Created on 17.11.15.
 */
public class QueryIterator implements Iterator<Query> {

    private QueryList queryList;
    private Integer queryIterator;
    List<String> idList;

    public QueryIterator(QueryList parent, long modifiedSince){
        queryList = parent;
        idList = queryList.idList(modifiedSince);
        queryIterator = 0;
    }

    @Override
    public boolean hasNext() {
        if (queryIterator >= idList.size())
            return false;
        return true;
    }

    @Override
    public Query next() {
        try {
            String queryID = idList.get(queryIterator++);
            return queryList.get(queryID);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
