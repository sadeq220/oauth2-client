package bmi.ir.ssoclient.controller;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
@RequestMapping("/air")
public class OpenController {
    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter filterChainProxy;// spring security FilterChainProxy
    @RequestMapping("/user")
    public Properties testFilterChain(){
        Properties properties = new Properties();
        properties.put("name","sadeq");
        properties.put("age",27);
        return properties;
    }
}
