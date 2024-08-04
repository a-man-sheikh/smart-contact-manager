package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/user")
public class UserController {
	
@Autowired
UserRepository userRepository;

@Autowired
ContactRepository contactRepository;
	
	@ModelAttribute
	void addCommonData(Model model, Principal principal) {
		String username= principal.getName();
		
		  User user = userRepository.getUserByUserName(username);
		  model.addAttribute("user",user);
	}
	
	@GetMapping("/index")
	public String dash() {
					   
		return "normal/user_dashboard";
	}
	@GetMapping("/add-contact")
	public String addContact(Model m ) {
		m.addAttribute("title","Add Contact - Smart Contact Manager");
		m.addAttribute("contact",new Contact());
		return "normal/addcontact";
	}
	
	
	//processing add contact form
	  @PostMapping("/process-contact")
	    public String processContact(
	           @ModelAttribute Contact contact,
	         
	            @RequestParam("myimage") MultipartFile file,
	            
	            Principal principal,HttpSession session) {

	    

	        try {
	            String username = principal.getName();
	            User user = userRepository.getUserByUserName(username);
	            user.getContacts().add(contact);
	            contact.setUser(user);

	            if (!file.isEmpty()) {
	                // Get the file and set the image field in the contact entity
	                contact.setImage(file.getOriginalFilename());

	                // Save the file to the specified location
	                File saveFile = new ClassPathResource("static/img").getFile();
	                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
	                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            }
				else{
					contact.setImage("contact.png");
				}

	         
	            userRepository.save(user);
	            session.setAttribute("message", new Message("Your contact is added !! Add more ..","alert-success"));
				
		
	

	        } catch (Exception e) {
	            e.printStackTrace();
	        	session.setAttribute("message", new Message("Something went wrong !! Try again .."+e.getMessage(),"alert-danger"));
				
	         
	            return "normal/addcontact";
	        }

	        return "redirect:/user/add-contact";
	    }
	  
	  @GetMapping("/show-contacts/{page}")
	  public String showContacts(@PathVariable("page") Integer page, Model model,Principal principal) {
		  model.addAttribute("title","view contacts - Smart Contact Manager");
		  
		  String userName = principal.getName();
		 User user= this.userRepository.getUserByUserName(userName);
		 Pageable pageable= PageRequest.of(page, 5);
		  
		Page<Contact> contact =  this.contactRepository.findContactsByUser(user.getId(),pageable);
		  model.addAttribute("contact",contact);
		  model.addAttribute("currentPage",page);
		  model.addAttribute("totalPages",contact.getTotalPages());
		  return "normal/show_contacts";
		  
	  }
	  @GetMapping("/error")
	  public String error(){
		  return "normal/error-page";
		  
	  }
	  @GetMapping("/{cid}/show-details")
	  public String showDetails(@PathVariable("cid") Integer cid,Model model,Principal principal) {
		  String userName = principal.getName();
	 User user = this.userRepository.getUserByUserName(userName);
	 Contact contact= this.contactRepository.getReferenceById(cid);
	 try {

		 if(user.getId()==contact.getUser().getId()) {
			 model.addAttribute("contact",contact);
		 }
		 else {
			 return "redirect:/user/error";
		 }
		 
	 }
	 catch(Exception e) {
		 return "redirect:/user/error";
	 }
		
		  return "normal/show_details";
	  }
	  @GetMapping("/delete/{cid}")
	  public String delete(@PathVariable("cid") Integer cid,Principal principal,HttpSession session){
		  String userName = principal.getName();
			 User user = this.userRepository.getUserByUserName(userName);
		  Contact contact = this.contactRepository.getReferenceById(cid);
		 
		  try {

				 if(user.getId()==contact.getUser().getId()) {
					 this.contactRepository.delete(contact);
					 session.setAttribute("message", new Message("Contact deleted succesfully..","success"));
				
				 }
				 else {
					 return "redirect:/user/error";
				 }
				 
			 }
			 catch(Exception e) {
				 return "redirect:/user/error";
			 }
				
		  
			 return "redirect:/user/show-contacts/0";
		  
	  }
	  @GetMapping("/update/{cid}")
	  public String update(@PathVariable("cid") Integer cid,Principal principal,Model model) {
		  String userName = principal.getName();
			 User user = this.userRepository.getUserByUserName(userName);
			 Contact contact= this.contactRepository.getReferenceById(cid);
			 try {

				 if(user.getId()==contact.getUser().getId()) {
					 model.addAttribute("contact",contact);
				 }
				 else {
					 return "redirect:/user/error";
				 }
				 
			 }
			 catch(Exception e) {
				 return "redirect:/user/error";
			 }
		  return "normal/update_form";
		  
	  }
	  @PostMapping("/update-contact/{cid}")
	    public String updateontact(@PathVariable("cid") Integer cid,
	           @ModelAttribute Contact contact,
	         
	            @RequestParam("myimage") MultipartFile file,
	            
	            Principal principal,HttpSession session) {

	    

	        try {
	            String username = principal.getName();
	            User user = userRepository.getUserByUserName(username);
	            user.getContacts().add(contact);
	            contact.setUser(user);

	            if (!file.isEmpty()) {
	                // Get the file and set the image field in the contact entity
	                contact.setImage(file.getOriginalFilename());

	                // Save the file to the specified location
	                File saveFile = new ClassPathResource("static/img").getFile();
	                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
	                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            }
				else{
					contact.setImage("contact.png");
				}

	         
	            userRepository.save(user);
	            session.setAttribute("message", new Message("Your contact is updated !! Add more ..","alert-success"));
				
		
	

	        } catch (Exception e) {
	            e.printStackTrace();
	        	session.setAttribute("message", new Message("Something went wrong !! Try again .."+e.getMessage(),"alert-danger"));
				
	         
	            return "normal/addcontact";
	        }

	        return "redirect:/user/show-contacts/0";
	    }
	  //your  profile handler
	  @GetMapping("/profile")
	  public String yourProfile(Model model) {
		  model.addAttribute("title","Smart Contact Manager - Profile ");
		  return "normal/profile";
	  }
}
