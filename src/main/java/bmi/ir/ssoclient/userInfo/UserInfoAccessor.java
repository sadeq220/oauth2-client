package bmi.ir.ssoclient.userInfo;

import bmi.ir.ssoclient.userInfo.model.UserInfoModel;

public interface UserInfoAccessor {
    UserInfoModel getIdentity(String nationalId);
}
