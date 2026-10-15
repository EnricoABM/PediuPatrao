package com.umc.pediupatrao.entity.permission;

/**
 * Classe para enumeração das permissões relacionadas as operações sobre
 * {@link Usuario} do sistema
 * 
 * @author Enrico Bertozzi
 * @version 1.0
 */
public enum UserPermission implements Permission {
    USER_CREATE,
    USER_READ,
    USER_READ_ALL,
    USER_UPDATE,
    USER_DELETE;

    @Override
    public String getName() {
        return this.name();
    }
}
