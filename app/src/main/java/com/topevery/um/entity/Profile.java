package com.topevery.um.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author wujie
 */
public class Profile {
    @SerializedName("UserId")
    private String id;
    @SerializedName("UserName")
    private String name;
    @SerializedName("MobileNum")
    private String mobileNum;
    @SerializedName("UserDept")
    private String deptId;
    @SerializedName("UserDeptName")
    private String deptName;
    @SerializedName("FId")
    private int fId;
    @SerializedName("RoleIds")
    private String roleIds;
    @SerializedName("UserType")
    private int userType;
    @SerializedName("LoginMsg")
    private String loginMsg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getLoginMsg() {
        return loginMsg;
    }

    public void setLoginMsg(String loginMsg) {
        this.loginMsg = loginMsg;
    }

}
