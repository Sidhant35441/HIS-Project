package com.his.admin.service;

import java.util.List;
import java.util.Map;

import com.his.admin.domains.AppAccount;

public interface AdminService {

	public boolean registerAccount(AppAccount appAccount);

	public String findByEmail(String emailId);
	
	public AppAccount findByAccountId(String accId);
	
	public boolean updateAccountSw(String accId,String activeSw);
	
	public List<AppAccount> findAllAppAccounts();
	
	public Map<Boolean, String>  login(String email, String pwd);
	

	
}
