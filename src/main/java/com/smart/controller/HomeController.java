package com.smart.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		
		
		
		
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - Smart Contact Manager");
		
		
		
		
		return "about";
	}
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Sign-Up - Smart Contact Manager");
		model.addAttribute("user",new User());	
		
		
		
		
		return "signup";
	}
	//handler for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,@RequestParam(value="agreement",defaultValue="false")boolean agreement, Model model,HttpSession session){
		try {
			if(!agreement) {
				System.out.println("you have not agreed the terms and conditons");
				throw new Exception("you have not agreed terms and conditions");
			}
			if(bindingResult.hasErrors()) {
				model.addAttribute("user",user);
				return "signup";
				
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			User result=this.userRepository.save(user);
			
			model.addAttribute("user",new User());

			System.out.println("Agreement"+agreement);
			System.out.println("User"+user);
			
	session.setAttribute("message", new Message("Successfully Registered","alert-success"));
			
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(),"alert-danger"));
			
			return "signup";
			
		}
		
		
		return "signup";

		
	}
	
	
	
	@GetMapping("/signin")
	public String login(Model model) {
		model.addAttribute("title","Login - Smart Contact Manager");
		return "/login";
	}



}
