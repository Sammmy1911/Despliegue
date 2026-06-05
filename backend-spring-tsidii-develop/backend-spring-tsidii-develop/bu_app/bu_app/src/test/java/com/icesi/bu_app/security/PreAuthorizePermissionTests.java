package com.icesi.bu_app.security;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import com.icesi.bu_app.controller.rest.ProgressRestController;
import com.icesi.bu_app.controller.rest.RoutineController;

public class PreAuthorizePermissionTests {

    @Test
    void routineController_getByTrainee_hasPreAuthorizeCallingPermissionService() throws NoSuchMethodException {
        Method m = RoutineController.class.getMethod("getByTrainee", Integer.class);
        PreAuthorize pa = m.getAnnotation(PreAuthorize.class);
        assertNotNull(pa, "getByTrainee should have @PreAuthorize");
        String expr = pa.value();
        assertTrue(expr.contains("permissionService.canViewTrainee") || expr.contains("permissionService.isAdmin"),
                "Expression should delegate to permissionService");
    }

    @Test
    void progressController_search_hasPreAuthorizeCallingPermissionService() throws NoSuchMethodException {
        Method m = ProgressRestController.class.getMethod("searchByRange", String.class, String.class, Integer.class);
        PreAuthorize pa = m.getAnnotation(PreAuthorize.class);
        assertNotNull(pa, "searchByRange should have @PreAuthorize");
        String expr = pa.value();
        assertTrue(expr.contains("permissionService.canViewTrainee") || expr.contains("permissionService.isAdmin"),
                "Expression should delegate to permissionService");
    }

    @Test
    void progressController_aggregate_hasPreAuthorizeCallingPermissionService() throws NoSuchMethodException {
        Method m = ProgressRestController.class.getMethod("aggregate", String.class, String.class, String.class, Integer.class);
        PreAuthorize pa = m.getAnnotation(PreAuthorize.class);
        assertNotNull(pa, "aggregate should have @PreAuthorize");
        String expr = pa.value();
        assertTrue(expr.contains("permissionService.canViewTrainee") || expr.contains("permissionService.isAdmin"),
                "Expression should delegate to permissionService");
    }
}
