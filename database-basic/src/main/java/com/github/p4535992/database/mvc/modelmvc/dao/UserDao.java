package com.github.p4535992.database.mvc.modelmvc.dao;

import java.util.List;

import com.github.p4535992.database.mvc.modelmvc.User;

public interface UserDao {

	User findByName(String name);
	
	List<User> findAll();

}