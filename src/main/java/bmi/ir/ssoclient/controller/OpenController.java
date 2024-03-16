package bmi.ir.ssoclient.controller;

import bmi.ir.ssoclient.controller.model.UserInfoDto;
import bmi.ir.ssoclient.userInfo.UserInfoAccessor;
import bmi.ir.ssoclient.userInfo.model.UserInfoModel;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/air")
public class OpenController {
    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter filterChainProxy;// spring security FilterChainProxy
    @Autowired
    private UserInfoAccessor userInfoAccessor;

    @RequestMapping("/user-info")
    public UserInfoDto testFilterChain(){
        UserInfoModel identity = userInfoAccessor.getIdentity("0019440619");
        return UserInfoDto.create(identity);
    }
}
