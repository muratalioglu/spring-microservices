package com.example.gateway.filters;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class UserContextHolder {

    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    public static UserContext getContext() {
        UserContext context = userContext.get();
        if (context == null) {
            context = createEmptyContext();
            userContext.set(context);
        }
        return context;
    }

    public static void setContext(UserContext context) {
        Assert.notNull(context, "Only non-null instances are permitted");
        userContext.set(context);
    }

    public static UserContext createEmptyContext() {
        return new UserContext();
    }
}
