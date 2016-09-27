/*
 * Copyright (c) 2016. Sergey V. Katunin <sergey.blaster@gmail.com>
 * Licensed under the Apache License, Version 2.0
 */

package com.github.wellcomer.query3.core;

import java.lang.Exception;
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
     * @throws Exception
     */
    List<String> read (String dataID) throws Exception;

    /**
     * Запись данных.
     * @param dataID Идентификатор данных в хранилище (имя файла или записи в БД).
     * @param data Строка с данными.
     * @throws Exception
     */
    void write (String dataID, String data) throws Exception;

    /**
     * Коммит изменений.
     * @throws Exception
     */
    void commit () throws Exception;

    /**
     * Получить состояние флага автокоммита.
     * @return Флаг автокоммита.
     */
    boolean autoCommit ();

    /**
     * Установить флаг автокоммита.
     * @param flagAutoCommit Флаг автокоммита (true|false).
     */
    void autoCommit (boolean flagAutoCommit);

    /**
     * Получить номер новой заявки.
     * @return номер заявки.
     * @throws Exception
     */
    Integer getNewNumber() throws Exception;

    /**
     * Установить номер новой заявки.
     * @param queryNumber номер заявки.
     * @throws Exception
     */
    void setNewNumber(Integer queryNumber) throws Exception;

    /**
     * Получить заявку.
     * @param queryNumber номер заявки.
     * @return объект с загруженной заявкой.
     * @throws Exception
     */
    Query get(Integer queryNumber) throws Exception;

    /**
     * Получить заявку.
     * @param queryID идентификатор заявки.
     * @return объект с загруженной заявкой.
     * @throws Exception
     */
    Query get(String queryID) throws Exception;

    /**
     * Добавить новую заявку (номер присваивается автоматически).
     * @param query объект с заявкой.
     * @throws Exception
     */
    void add(Query query) throws Exception;

    /**
     * Добавить заявку.
     * @param queryNumber номер заявки.
     * @param query объект с заявкой.
     * @throws Exception
     */
    void add(Integer queryNumber, Query query) throws Exception;

    /**
     * Количество объектов(заявка) в хранилище.
     * @return Количество
     */
    Integer size ();

    /**
     * Список идентификаторов заявок.
     * @param modifiedSince Референсное время для отслеживания модификации.
     * @return Список строк (идентификаторов).
     */
    List<String> idList (long modifiedSince);

    /**
     * Деструктор.
     * @throws Exception
     */
    void close () throws Exception;
}
