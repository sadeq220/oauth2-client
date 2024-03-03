package bmi.ir.ssoclient.userInfo.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserInfoModel implements OAuth2User {
    private String nationalId;
    private Content content;
    private String role;
    private List<String> authorities;
    private List<GrantedAuthority> springAuthorities; // converted from @field authorities, not json serde included because there is no Accessor method for this field

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }
    @Override
    public List<GrantedAuthority> getAuthorities() {
        return springAuthorities;
    }
    @Override
    public String getName() {
        return nationalId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
        this.springAuthorities= authorities.stream()
                .map(authority-> (GrantedAuthority) () -> authority)
                .collect(Collectors.toList());
    }
    public List<String> getAuthoritiesAsString(){
        return authorities;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
