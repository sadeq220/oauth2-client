package bmi.ir.ssoclient.userInfo;

import bmi.ir.ssoclient.userInfo.model.Content;
import bmi.ir.ssoclient.userInfo.model.UserInfoModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserManagementUserInfoAccessor implements UserInfoAccessor{
    private final String userManagementAddress;
    private final String userManagementEndpointPath;

    public UserManagementUserInfoAccessor(@Value("${userManagement.address}") String userManagementAddress,
                                          @Value("${userManagement.userInfo.endpoint}") String userManagementEndpoint) {
        this.userManagementAddress = userManagementAddress;
        this.userManagementEndpointPath = userManagementEndpoint;
    }

    @Override
    public UserInfoModel getIdentity(String nationalId) {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAuthorities(List.of("receipt_read","export_update"));
        userInfoModel.setNationalId(nationalId);
        userInfoModel.setRole("XYZ");
        Content content = new Content();
        content.setGoods(List.of(7,8));
        content.setInventories(List.of(1,2));
        content.setOrganizations(List.of(3));
        userInfoModel.setContent(content);
        return userInfoModel;
    }
}
