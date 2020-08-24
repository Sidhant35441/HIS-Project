package com.his.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.his.admin.domains.AppAccount;
import com.his.admin.service.AdminService;
import com.his.constant.AppConstants;
import com.his.properties.AppProperties;

@Controller
public class AdminController {

	@Autowired(required = true)
	private AdminService adminService;
	
	@Autowired
	private AppProperties appProperties;
	
	
	@RequestMapping(value = "/accReg", method = RequestMethod.GET)
	public String accRegForm(Model model) {
		// Creating empty model object
		AppAccount accModel = new AppAccount();

		// add cwModel object to Model scope
		model.addAttribute("accModel", accModel);

		initForm(model);

		return "accReg";
	}

	@RequestMapping(value = "/accReg", method = RequestMethod.POST)
	public String accReg(@ModelAttribute("accModel") AppAccount appAccModel, Model model) {
		try {
			// call Service layer method
			boolean isSaved = adminService.registerAccount(appAccModel);

			Map<String, String> map = appProperties.getProperties();
			if (isSaved) {
				// Display success message
				model.addAttribute(AppConstants.SUCCESS, map.get(AppConstants.CW_REG_SUCCESS));
			} else {
				// Display failure message
				model.addAttribute(AppConstants.FAILURE, map.get(AppConstants.CW_REG_FAIL));
			}
			initForm(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "accReg";
	}
	
	@RequestMapping(value="/login")
	public String loginForm() {
		return "login";
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String login(HttpServletRequest req, Model model) {
		String email=req.getParameter("email");
		String pwd=req.getParameter("pwd");
		Map<Boolean, String> result = adminService.login(email, pwd);
		if(result.containsKey(true)) {
			return result.get(true);
		}else {
			model.addAttribute(AppConstants.FAILURE, appProperties.getProperties().get(result.get(false)));
		}
		return "login";
	}
	
	private void initForm(Model model) {
		List<String> gendersList = new ArrayList<>();
		gendersList.add("Male");
		gendersList.add("Fe-Male");
		model.addAttribute("gendersList", gendersList);
	}

	@RequestMapping("/accReg/validateEmail")
	public @ResponseBody String checkEmailValidity(HttpServletRequest req, Model model) {
		String emailId = req.getParameter("email");
		return adminService.findByEmail(emailId);
	}


	@RequestMapping(value = "/viewAccounts")
	public String viewAccounts(Model model) {
		// calling service layer method
		List<AppAccount> accounts = adminService.findAllAppAccounts();

		// store accounts in model scope
		model.addAttribute(AppConstants.APP_ACCOUNTS, accounts);
		return "viewAccounts"; // view name
	}

	@RequestMapping(value = "/delete")
	public String deleteAccount(HttpServletRequest req, Model model) {
		// capture query param value
		String accId = req.getParameter("accId");

		// call service layer method
		boolean isDeleted = adminService.updateAccountSw(accId, AppConstants.IN_ACTIVE_SW);

		// calling service layer method
		List<AppAccount> accounts = adminService.findAllAppAccounts();

		// store accounts in model scope
		model.addAttribute(AppConstants.APP_ACCOUNTS, accounts);

		if (isDeleted) {
			String succMsg = appProperties.getProperties().get(AppConstants.ACC_DE_ACTIVATE_SUCC_MSG);
			model.addAttribute(AppConstants.SUCCESS, succMsg);
		} else {
			String errMsg = appProperties.getProperties().get(AppConstants.ACC_DE_ACTIVATE_ERR_MSG);
			model.addAttribute(AppConstants.FAILURE, errMsg);
		}
		return "viewAccounts";
	}
	
}
