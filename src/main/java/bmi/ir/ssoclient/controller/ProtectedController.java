package bmi.ir.ssoclient.controller;

import bmi.ir.ssoclient.controller.model.UserInfoDto;
import bmi.ir.ssoclient.userInfo.model.UserInfoModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/protected")
public class ProtectedController {

    @GetMapping("/user-info")
    public Object userInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        return UserInfoDto.create((UserInfoModel) principal);
    }
}
