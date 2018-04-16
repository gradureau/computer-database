package com.excilys.gradureau.computer_database.persistance.dao;

public abstract class DAO<T> {
	public abstract T find(long id);
	public abstract T create(T obj);
	public abstract T update(T obj);
	public abstract T delete(T obj);
}
