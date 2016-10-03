/*
 * Copyright (c) 2016. Sergey V. Katunin <sergey.blaster@gmail.com>
 * Licensed under the Apache License, Version 2.0
 */

package com.github.wellcomer.query3.core;



import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * <h3>MapDB хранилище.</h3>
 * Created on 22.09.16.<br>
 */

public class MapDBStorage implements QueryStorage {

    private DB db = null;
    private final ConcurrentMap<Integer,String> mapQuery;
    private final ConcurrentNavigableMap<Integer,Long> mapTime;
    private final String lineSeparator;
    private boolean autoCommit = true;

    public MapDBStorage(String dbPath, String charsetName) {

        db = DBMaker.fileDB(dbPath).closeOnJvmShutdown().transactionEnable().make();
        mapQuery = db.hashMap("query3", Serializer.INTEGER, Serializer.STRING).counterEnable().createOrOpen();
        mapTime = db.treeMap("time", Serializer.INTEGER, Serializer.LONG).createOrOpen();
        lineSeparator = System.getProperty("line.separator");
    }

    @Override
    public boolean autoCommit () {
        return autoCommit;
    }

    @Override
    public void autoCommit (boolean flagAutoCommit){
        this.autoCommit = flagAutoCommit;
    }

    @Override
    public void commit () throws Exception{
        db.commit();
    }

    @Override
    public List<String> read(String key) throws Exception {
        Integer queryID = Integer.parseInt(key);
        String v = mapQuery.get(queryID);
        return Arrays.asList(v.split(lineSeparator));
    }

    @Override
    public void write(String key, String data) throws Exception {
        Integer queryID = Integer.parseInt(key);
        mapQuery.put(queryID, data);
        mapTime.put(queryID, System.currentTimeMillis());
        if (autoCommit)
            db.commit();
    }

    @Override
    public Integer getNewNumber() throws Exception {
        return mapQuery.size() + 1;
    }

    @Override
    public void add(Integer queryNumber, Query query) throws Exception {

        String output = "", k, v;

        if (!query.containsKey("version"))
            output = String.format("version:1%s", lineSeparator);
        if (!query.containsKey("num"))
            output += String.format("num:%s%s", queryNumber, lineSeparator);

        for (Map.Entry<String,String> entry : query.entrySet()){
            k = entry.getKey();
            v = entry.getValue();
            if (k.equals("n") || k.equals("f") || k.equals(""))
                continue;
            output += String.format("%s:%s%s", k, v, lineSeparator);
        }
        write(queryNumber.toString(), output);
    }

    @Override
    public Integer size() {
        return mapQuery.size();
    }

    @Override
    public List<String> idList(long modifiedSince) {

        Integer k;
        Long v;

        List<String> idList = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : mapTime.entrySet()){
            k = entry.getKey();
            v = entry.getValue();
            if (v > modifiedSince)
                idList.add(k.toString());
        }
        return idList;
    }

    @Override
    public void close() throws Exception {
        commit();
        db.close();
    }
}
