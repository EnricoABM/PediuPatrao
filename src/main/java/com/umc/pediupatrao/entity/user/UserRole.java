package com.umc.pediupatrao.entity.user;

import java.util.List;

import static com.umc.pediupatrao.entity.user.UserPermission.*;

public enum UserRole {
    ADMIN(List.of(
        USER_CREATE,
        USER_READ,
        USER_READ_ALL,
        USER_DELETE,
        USER_UPDATE
    )),
    GERENTE(List.of(

    )),
    ATENDENTE(List.of(

    ));

    private UserRole(List<UserPermission> permissions) {
        this.permissions = permissions;
    }

    private List<UserPermission> permissions;

    public List<UserPermission> getPermissions() {
        return permissions;
    }
}
