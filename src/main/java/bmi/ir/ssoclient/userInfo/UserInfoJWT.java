package bmi.ir.ssoclient.userInfo;

import bmi.ir.ssoclient.userInfo.model.UserInfoModel;

public interface UserInfoJWT{
    String createJWT(UserInfoModel userInfoModel);
}
