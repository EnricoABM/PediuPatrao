package com.umc.pediupatrao.entity.user;

import static com.umc.pediupatrao.entity.permission.UserPermission.*;
import static com.umc.pediupatrao.entity.permission.ClientPermission.*;
import static com.umc.pediupatrao.entity.permission.ProductPermission.*;

import java.util.List;

import com.umc.pediupatrao.entity.permission.Permission;

/**
 * Classe para enumeraçãao das roles do sistema e o permissionamento de cada
 * tipo de usuário
 * 
 * @author Enrico Bertozzi
 * @version 1.0
 */
public enum UserRole {
    ADMIN(List.of(
            USER_CREATE,
            USER_READ,
            USER_READ_ALL,
            USER_DELETE,
            USER_UPDATE)),
    GERENTE(List.of(
            USER_READ,
            CLIENT_READ,
            CLIENT_READ_ALL,
            CLIENT_CREATE,
            CLIENT_UPDATE,
            CLIENT_DELETE,
            PRODUCT_CREATE,
            PRODUCT_READ,
            PRODUCT_READ_ALL,
            PRODUCT_DELETE,
            PRODUCT_UPDATE
        )),
    ATENDENTE(List.of(
            USER_READ,
            CLIENT_READ,
            CLIENT_READ_ALL,
            CLIENT_CREATE,
            CLIENT_UPDATE,
            PRODUCT_READ,
            PRODUCT_READ_ALL
        ));

    private List<Permission> permissions;
    
    private UserRole(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
