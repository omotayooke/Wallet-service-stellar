package com.interswitch.service;

import com.interswitch.model.User;

public interface UserService {
	public User findUserByEmail(String email);

	public void saveUser(User user);
}
