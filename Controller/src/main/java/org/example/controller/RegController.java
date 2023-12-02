package org.example.controller;

import com.example.grpc.AuthServiceGrpc;
import com.example.grpc.AuthServiceOuterClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reg")
public class RegController {

    @Autowired
    private AuthServiceGrpc.AuthServiceBlockingStub stub;

    @GetMapping("")
    public String getReg() {
        return "registration";
    }

    @PostMapping("")
    public String regUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        AuthServiceOuterClass.Request request = AuthServiceOuterClass.Request.newBuilder()
                .setUsername(username).setPassword(password).build();
        AuthServiceOuterClass.Response response = stub.registration(request);
        if (response.getSuccess() == true) {
            return "redirect:/login";
        }
        model.addAttribute("error", response.getResponse());
        return "registration";
    }

}
