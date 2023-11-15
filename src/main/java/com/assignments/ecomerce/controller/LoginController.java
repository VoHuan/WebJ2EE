package com.assignments.ecomerce.controller;

import com.assignments.ecomerce.dto.UserDTO;
import com.assignments.ecomerce.model.Users;
import com.assignments.ecomerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/fragments")
    public String page() {
        return "fragments";
    }

    @GetMapping("/login")
    public String pageLogin() {
        return "login";
    }

    @GetMapping("/index")
    public String pageIndex() {
        return "index";
    }

    @GetMapping("/forgotPassword")
    public String pageForgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register-new")
    public String pageRegister(@ModelAttribute("userDTO") UserDTO userDTO
            , BindingResult result, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("userDTO", userDTO);
                return "register";
            }
            String username = userDTO.getUserName();
            Users user = userService.findByUsername(username);
            if (user != null) {
                model.addAttribute("userDTO", userDTO);
                System.out.println("user not null");
                model.addAttribute("emailError", "Your email has been registered!");
                return "register";
            }
            if (userDTO.getPassWord().equals(userDTO.getRepeatPassword())) {
                //userDTO.setPassWord(passwordEncoder.encode(userDTO.getPassWord()));
                userService.save(userDTO);
                System.out.println("success");
                model.addAttribute("success", "Register successfully!");
                model.addAttribute("userDTO", userDTO);
            } else {
                model.addAttribute("userDTO", userDTO);
                model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
                System.out.println("password not same");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "The server has been wrong!");
        }
        return "register";
    }
}
