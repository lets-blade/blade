package com.blade.ioc.service.test;

import com.blade.ioc.annotation.Component;

@Component
public class UserServiceImpl implements UserService {

	@Override
	public String getName() {
		return "blade";
	}

}
