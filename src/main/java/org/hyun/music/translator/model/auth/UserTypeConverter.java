package org.hyun.music.translator.model.auth;

import javax.persistence.AttributeConverter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class UserTypeConverter implements AttributeConverter<UserType, String> {

    EnumMap<UserType, String> enumToStringMap = new EnumMap<>(UserType.class){{
        put(UserType.SUPER_USER, "SU");
        put(UserType.USER, "U");
    }};

    Map<String, UserType> stringToEnumMap = new HashMap<>(){{
        put("SU", UserType.SUPER_USER);
        put("U", UserType.USER);
    }};

    @Override
    public String convertToDatabaseColumn(UserType userType){ return enumToStringMap.get(userType); }

    @Override
    public UserType convertToEntityAttribute(String s) { return stringToEnumMap.get(s); }
}
