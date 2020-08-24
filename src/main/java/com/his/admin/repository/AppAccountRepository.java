package com.his.admin.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.his.admin.entities.AppAccountEntity;

@Repository("appAccRepository")
public interface AppAccountRepository extends JpaRepository<AppAccountEntity, Serializable> {

	@Query(name = "from AppAccountEntity where email=:emailId")
	public AppAccountEntity findByEmail(String emailId);

}
