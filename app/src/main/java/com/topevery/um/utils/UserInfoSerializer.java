package com.topevery.um.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.topevery.um.entity.Profile;

import java.lang.reflect.Type;

/**
 * @author wujie
 */
public class UserInfoSerializer implements JsonSerializer<Profile> {

    @Override
    public JsonElement serialize(Profile profile, Type type, JsonSerializationContext jsonSerializationContext) {
        if (profile == null) {
            return new JsonNull();
        }

        JsonObject object = new JsonObject();
        object.addProperty("userId", profile.getId());
        object.addProperty("userName", profile.getName());
        object.addProperty("mobileNum", profile.getMobileNum());
        object.addProperty("userDept", profile.getDeptId());
        object.addProperty("userDeptName", profile.getDeptName());
        object.addProperty("fId", profile.getfId());
        object.addProperty("roleIds", profile.getRoleIds());
        object.addProperty("userType", profile.getUserType());
        object.addProperty("loginMsg", profile.getLoginMsg());
        return object;
    }
}
