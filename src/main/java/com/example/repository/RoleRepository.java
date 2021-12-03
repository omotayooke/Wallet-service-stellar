package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Role;

//@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer>{
	Role findByRole(String role);

}
