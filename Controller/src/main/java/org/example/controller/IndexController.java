package org.example.controller;

import com.example.grpc.ImageRequest;
import com.example.grpc.ImageResponse;
import com.example.grpc.ImageServiceGrpc;
import com.example.grpc.SuccessResponse;
import com.google.protobuf.ByteString;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private ImageServiceGrpc.ImageServiceBlockingStub stub;

    @GetMapping("/")
    public String getIndex(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        int userId = (int) session.getAttribute("id");
        ImageRequest request = ImageRequest.newBuilder().setUserID(userId).build();
        ImageResponse response = stub.getAllImages(request);
        List<String> images = response.getFilenameList();
        for (String img : images) {
            System.out.println(images);
        }
        model.addAttribute("image", images);
        return "index";
    }

    @GetMapping("/continue")
    public String getContinue() {
        return "redirect:/";
    }

    @PostMapping("/download")
    public String downloadImage(HttpServletRequest request) throws ServletException, IOException {
        int userId = (int)request.getSession().getAttribute("id");
        Part filePart = request.getPart("file");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        ByteString fileData = ByteString.readFrom(filePart.getInputStream());
        ImageRequest req = ImageRequest.newBuilder().setUserID(userId).setFilename(fileName).setImage(fileData).build();
        SuccessResponse response = stub.addImage(req);
        return "redirect:/";
    }

}
