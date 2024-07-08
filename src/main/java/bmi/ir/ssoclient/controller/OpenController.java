package bmi.ir.ssoclient.controller;

import bmi.ir.ssoclient.controller.model.UserInfoDto;
import bmi.ir.ssoclient.userInfo.UserInfoAccessor;
import bmi.ir.ssoclient.userInfo.model.UserInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Properties;


@RestController
@RequestMapping("/air")
public class OpenController {
    @Autowired
   private WebFilterChainProxy webFilterChainProxy;// spring-security WebFilterChainProxy, a WebFilter which delegates the request to a list of SecurityWebFilterChain
    @Autowired
    private UserInfoAccessor userInfoAccessor;

    @RequestMapping("/user-info")
    public Mono<UserInfoDto> testFilterChain(){
        UserInfoModel identity = userInfoAccessor.getIdentity("0019440619");
        return Mono.just(UserInfoDto.create(identity));
    }

    /**
     * To protect against CSRF exploit backend services
     * safe Methods(GET, HEAD, OPTIONS, and TRACE) Must be Read-only.
     *
     *  Synchronizer Token Pattern(best practice) expects csrf-tokens be present in Header(or request param) of not safe requests.
     */
    @RequestMapping("/csrf-token")
    public Mono<Properties> getCsrfToken(ServerWebExchange exchange){
        Mono<CsrfToken> csrfTokenMono = exchange.getAttribute(CsrfToken.class.getName());
        return csrfTokenMono.map(csrfToken -> {
            Properties properties = new Properties();
            properties.put("token", csrfToken.getToken());
            properties.put("headerName", "X-XSRF-TOKEN");
            return properties;
        });
    }
}
