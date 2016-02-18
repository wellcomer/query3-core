/*
 * Copyright (c) 2016. Sergey V. Katunin <sergey.blaster@gmail.com>
 * Licensed under the Apache License, Version 2.0
 */

package com.github.wellcomer.query3.core;

import java.io.IOException;
import java.util.List;

/**
 * <h3>Интерфейс хранилища заявок.</h3>
 * Created on 18.11.15.
 */
public interface QueryStorage {

    /**
     * Чтение данных.
     * @param dataID Идентификатор данных в хранилище (имя файла или записи в БД).
     * @return Список строк.
     * @throws IOException
     */
    List<String> read (String dataID) throws IOException;

    /**
     * Запись данных.
     * @param dataID Идентификатор данных в хранилище (имя файла или записи в БД).
     * @param data Строка с данными.
     * @throws IOException
     */
    void write (String dataID, String data) throws IOException;

    /**
     * Получить номер новой заявки.
     * @return номер заявки.
     * @throws IOException
     */
    Integer getNewNumber() throws IOException;

    /**
     * Установить номер новой заявки.
     * @param queryNumber номер заявки.
     * @throws IOException
     */
    void setNewNumber(Integer queryNumber) throws IOException;

    /**
     * Получить заявку.
     * @param queryNumber номер заявки.
     * @return объект с загруженной заявкой.
     * @throws IOException
     */
    Query get(Integer queryNumber) throws IOException;

    /**
     * Получить заявку.
     * @param queryID идентификатор заявки.
     * @return объект с загруженной заявкой.
     * @throws IOException
     */
    Query get(String queryID) throws IOException;

    /**
     * Добавить новую заявку (номер присваивается автоматически).
     * @param query объект с заявкой.
     * @throws IOException
     */
    void add(Query query) throws IOException;

    /**
     * Добавить заявку.
     * @param queryNumber номер заявки.
     * @param query объект с заявкой.
     * @throws IOException
     */
    void add(Integer queryNumber, Query query) throws IOException;

    /**
     * Количество заявок в хранилище.
     */
    Integer size ();

    /**
     * Список идентификаторов заявок.
     * @param modifiedSince Референсное время для отслеживания модификации.
     * @return Список строк (идентификаторов).
     */
    List<String> idList (long modifiedSince);
}
