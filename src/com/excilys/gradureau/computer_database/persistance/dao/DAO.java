package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.util.List;

import com.excilys.gradureau.computer_database.util.Page;

public abstract class DAO<T> {
	
	protected Connection connection;
	public DAO(Connection connection) {
		this.connection = connection;
	}
	public abstract T find(long id);
	public abstract List<T> findAll();
	public abstract T create(T obj);
	public abstract T update(T obj);
	public abstract void delete(T obj);
	/**
	 * This is a default implementation, it is inneficient as it is returning everything.
	 * Inheriting subclasses should offer a correct implementation.
	 * @param start >=0 is the offset
	 * @param resultsCount >=1 is the upper limit of retrieved objects
	 * @return a Page with according to the parametered offset and upper limit
	 */
	public Page<T> pagination(int start, int resultsCount){
		List<T> list = findAll();
		return new Page<T>(list,0,list.size());
	}
}
