package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.util.List;

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
}
