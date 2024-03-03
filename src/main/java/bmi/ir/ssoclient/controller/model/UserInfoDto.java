package bmi.ir.ssoclient.controller.model;

import bmi.ir.ssoclient.userInfo.model.UserInfoModel;

import java.util.List;

public class UserInfoDto {
    private String nationalId;
    private String role;
    private List<String> authorities;
    private ContentDto contentDto;

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public ContentDto getContentDto() {
        return contentDto;
    }

    public void setContentDto(ContentDto contentDto) {
        this.contentDto = contentDto;
    }
    public static UserInfoDto create(UserInfoModel userInfoModel){
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setNationalId(userInfoModel.getNationalId());
        userInfoDto.setRole(userInfoModel.getRole());
        ContentDto contentDto = new ContentDto();
        contentDto.setGoods(userInfoModel.getContent().getGoods());
        contentDto.setInventories(userInfoModel.getContent().getInventories());
        contentDto.setOrganizations(userInfoModel.getContent().getOrganizations());
        userInfoDto.setContentDto(contentDto);
        userInfoDto.setAuthorities(userInfoModel.getAuthoritiesAsString());
        return userInfoDto;
    }
}
