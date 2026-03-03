package com.vunum.SocietyAdmin.Controller;

import com.vunum.SocietyAdmin.DTO.RequestDTO;
import com.vunum.SocietyAdmin.Service.Services;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Common {
    @Autowired
    private Services adminService;

    @PostMapping("/login")
    public ResponseEntity<?> postLogin(@ModelAttribute RequestDTO.Commonrequest request,
                                       HttpServletRequest requests) {
        return adminService.loginPost(request, requests);
    }

    @GetMapping("/login")
    public ResponseEntity<?> getLogin(@RequestHeader("Authorization") String Auth,
                                      HttpServletRequest requests) {
        return adminService.loginGet(Auth, requests);
    }
}
