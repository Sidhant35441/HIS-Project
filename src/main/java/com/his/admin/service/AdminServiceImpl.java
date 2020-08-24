package com.his.admin.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.his.admin.domains.AppAccount;
import com.his.admin.entities.AppAccountEntity;
import com.his.admin.repository.AppAccountRepository;
import com.his.admin.utils.EmailUtils;
import com.his.admin.utils.PasswordUtils;
import com.his.constant.AppConstants;
import com.his.properties.AppProperties;

/**
 * This class is used to handler all business operations related to admin module
 * 
 * @author admin
 *
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {

	@Autowired(required = true)
	private AppAccountRepository appAccRepository;

	@Autowired(required = true)
	private EmailUtils emailUtils;

	@Autowired
	private AppProperties appProperties;
	
	@Override
	public boolean registerAccount(AppAccount appAcc) {
		// Convert model data to Entity data
		AppAccountEntity entity = new AppAccountEntity();
		BeanUtils.copyProperties(appAcc, entity);

		// Encrypt Password
		String encryptedPwd = PasswordUtils.encrypt(appAcc.getPassword());

		// Set Encrypted password to Entity obj
		entity.setPassword(encryptedPwd);

		// set Status as Active
		entity.setActiveSw(AppConstants.ACTIVE_SW);

		// Call Repository method
		entity = appAccRepository.save(entity);

		return (entity.getAccId() !=null) ? true : false;
	}

	public String getEmailBodyContent(AppAccount accModel, String fileName) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		StringBuffer body = new StringBuffer();
		String line = br.readLine();
		while (line != null) {
			if (line != null && !"".equals(line) && !"<br/>".equals(line)) {
				// process
				if (line.contains("USER_NAME")) {
					line = line.replace("USER_NAME", accModel.getFirstName() + " " + accModel.getLastName());
				}
				if (line.contains("APP_URL")) {
					line = line.replace("APP_URL", "<a href='http://localhost:7070/IES/'>IES URL</a>");
				}
				if (line.contains("APP_USER_EMAIL")) {
					line = line.replace("APP_USER_EMAIL", accModel.getEmail());
				}
				if (line.contains("APP_USER_PWD")) {
					line = line.replace("APP_USER_PWD", accModel.getPassword());
				}
				// Adding processed line to SB body
				body.append(line);
			}
			// read next line
			line = br.readLine();
		}
		// closing br
		br.close();
		return body.toString();
	}

	@Override
	public String findByEmail(String emailId) {
		AppAccountEntity entity = appAccRepository.findByEmail(emailId);
		return (entity == null) ? "Unique" : "Duplicate";
	}
	@Override
	public boolean updateAccountSw(String accId, String activeSw) {
		String fileName = null, mailSub = null, mailBody = null, password = null;
		try {
			// load existing record using accId
			AppAccountEntity entity = appAccRepository.findById(accId).get();
			if (entity != null) {
				// Setting Account Active Sw (Y|N)
				entity.setActiveSw(activeSw);
				// Updating Account
				appAccRepository.save(entity);
				AppAccount accModel = new AppAccount();
				BeanUtils.copyProperties(entity, accModel);

				// TODO:Need to complete email functionality
				if (activeSw.equals("Y")) {
					// send Email saying account activated
					try {
						// get file name
						fileName = appProperties.getProperties().get(AppConstants.ACTIVATE_EMAIL_FILE);
						// get mail subject
						mailSub = appProperties.getProperties().get(AppConstants.ACTIVATE_EMAIL_SUB);
						// decrypt the password
						password = PasswordUtils.decrypt(accModel.getPassword());
						// set decrypted password to accModel object password field
						accModel.setPassword(password);
						// get email body
						mailBody = getEmailBodyContent(accModel, fileName);
						// send email to activate registered cw/admin
						emailUtils.sendEmail(entity.getEmail(), mailSub, mailBody);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}// method
	@Override
	public List<AppAccount> findAllAppAccounts() {
		List<AppAccount> models = new ArrayList<AppAccount>();
		try {
			// call Repository method
			List<AppAccountEntity> entities = appAccRepository.findAll();

			if (entities.isEmpty()) {
			} else {
				// convert Entities to models
				for (AppAccountEntity entity : entities) {
					AppAccount model = new AppAccount();
					BeanUtils.copyProperties(entity, model);
					models.add(model);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return models;
	}


	@Override
	public AppAccount findByAccountId(String accId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Boolean, String> login(String email, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

}// class
