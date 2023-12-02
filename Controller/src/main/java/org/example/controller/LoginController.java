package org.example.controller;

import com.example.grpc.AuthServiceGrpc;
import com.example.grpc.AuthServiceOuterClass;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private AuthServiceGrpc.AuthServiceBlockingStub stub;

    @GetMapping("")
    public String getLogin() {
        return "login";
    }

    @PostMapping("")
    public String login(HttpSession session, Model model, @RequestParam String username, @RequestParam String password) {
        AuthServiceOuterClass.Request request = AuthServiceOuterClass.Request.newBuilder()
                .setUsername(username).setPassword(password).build();
        AuthServiceOuterClass.Response response = stub.login(request);
        if (!response.getSuccess()) {
            model.addAttribute("error", response.getResponse());
            return "login";
        }
        AuthServiceOuterClass.Request req = AuthServiceOuterClass.Request.newBuilder().setUsername(username).build();
        AuthServiceOuterClass.User res = stub.getUser(req);
        session.setAttribute("username", res.getUsername());
        session.setAttribute("id", res.getUserId());
        return "redirect:/";
    }
}