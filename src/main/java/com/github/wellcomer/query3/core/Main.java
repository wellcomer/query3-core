/*
 * Copyright (c) 2016. Sergey V. Katunin <sergey.blaster@gmail.com>
 * Licensed under the Apache License, Version 2.0
 */

package com.github.wellcomer.query3.core;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Main {

    Manifest getManifest (){

        Manifest manifest;

        URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
        try {
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            manifest = new Manifest(url.openStream());
        } catch (IOException E) {
            // handle
            return null;
        }
        return manifest;
    }

    /**
     * Получить экземпляр класса хранилища.
     * @param stgBackendName Имя бэкенда хранилища (file|mapdb).
     * @param dbPath Путь к базе данных.
     * @param dbName Имя базы данных.
     * @param charset Кодовая страница.
     * @return Список с номерами найденных заявок.
     */

    @Nullable
    public static QueryStorage getStgBackendInstance (String stgBackendName, String dbPath, String dbName, String charset){

        if (charset.isEmpty() || charset.equalsIgnoreCase("default"))
            charset = "UTF-8";

        switch (stgBackendName.toLowerCase()){
            case "file":
                if (dbName.equalsIgnoreCase("default"))
                    dbName = ".db";
                dbPath = Paths.get(dbPath, dbName).toString();
                return new FileStorage(dbPath, charset);
            case "mapdb":
                if (dbName.equalsIgnoreCase("default"))
                    dbName = "map.db";
                dbPath = Paths.get(dbPath, dbName).toString();
                return new MapDBStorage(dbPath, charset);
        }
        return null;
    }

    public static void main(String[] args) throws IOException {

        Main main = new Main();

        if (args.length == 5){
            switch (args[0]) {
                case "conv":

                    String[] stgBackendName = new String[2];
                    String[] dbPath = new String[2];
                    stgBackendName[0] = args[1];
                    dbPath[0] = args[2];
                    stgBackendName[1] = args[3];
                    dbPath[1] = args[4];

                    QueryStorage stg1 = null, stg2 = null;
                    Integer stg1Size = 0, stg2Size;

                    try {
                        stg1 = getStgBackendInstance(stgBackendName[0], dbPath[0], "", "UTF-8");
                        stg2 = getStgBackendInstance(stgBackendName[1], dbPath[1], "", "UTF-8");

                        stg1Size = stg1.size();
                        stg2Size = stg2.size();
                        System.out.printf("Backend1 storage size: %d\nBackend2 storage size: %d\n", stg1Size, stg2Size);
                    }
                    catch (Exception e) {
                        System.out.print("Failed to initialize storage backend!\n");
                        e.printStackTrace();
                        System.exit(1);
                    }

                    Integer queryNumber;
                    Query query;

                    stg2.autoCommit(false); // speedup

                    for (queryNumber = 1; queryNumber <= stg1Size; queryNumber++) {
                        try {
                            query = stg1.get(queryNumber);
                            stg2.add(queryNumber, query);
                            System.out.printf("\rProcessing: %d", queryNumber);
                            System.out.flush();
                        } catch (Exception e) {}
                    }
                    try {
                        stg1.close();
                        stg2.close();
                    }
                    catch (Exception e){
                        System.out.print("Unable to properly close the storage backend(s).\n");
                    }
                    System.out.print("\nDone!\n");
            }
        }
        else {
            Manifest manifest = main.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            String version = attributes.getValue("Implementation-Version");
            String build = attributes.getValue("Implementation-Build");
            System.out.printf("query3-core %s build %s (C) 2016 Sergey V. Katunin <sergey.blaster@gmail.com>\n", version, build);
        }
    }

}
