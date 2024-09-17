package com.myOnlineShoppingService.accountsService.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static String mapToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
