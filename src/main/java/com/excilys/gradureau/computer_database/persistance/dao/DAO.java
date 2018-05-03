package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.excilys.gradureau.computer_database.util.Page;

public abstract class DAO<T> {

    protected Connection connection;

    public DAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * find entity in database.
     *
     * @param id
     *            primary key
     * @return null if not found
     */
    public abstract Optional<T> find(long id);

    public abstract List<T> findAll();

    public abstract Optional<T> create(T obj);

    public abstract Optional<T> update(T obj);

    public abstract boolean delete(T obj);

    /**
     * This is a default implementation, it is inneficient as it is returning
     * everything. Inheriting subclasses should offer a correct
     *
     * @param start
     *            >=0 is the offset
     * @param resultsCount
     *            >=1 is the upper limit of retrieved objects
     * @return a Page with according to the parametered offset and upper limit
     */
    public Page<T> pagination(int start, int resultsCount) {
        List<T> list = findAll();
        return new Page<>(list, 0, list.size());
    }
    
    public abstract Page<T> filterBy(Map<String,String> criterias, int start, int resultsCount);
    
}
