package com.umc.pediupatrao.entity.user;

import static com.umc.pediupatrao.entity.permission.UserPermission.*;
import static com.umc.pediupatrao.entity.permission.ClientPermission.*;

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
            CLIENT_READ,
            CLIENT_READ_ALL,
            CLIENT_CREATE,
            CLIENT_UPDATE,
            CLIENT_DELETE)),
    ATENDENTE(List.of(
            CLIENT_READ,
            CLIENT_READ_ALL,
            CLIENT_CREATE,
            CLIENT_UPDATE));

    private List<Permission> permissions;
    
    private UserRole(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
