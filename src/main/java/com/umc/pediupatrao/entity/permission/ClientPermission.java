package com.umc.pediupatrao.entity.permission;

/**
 * Classe para enumeração das permissões relacionadas as operações sobre
 * {@link Cliente} do sistema
 * 
 * @author Enrico Bertozzi
 * @version 1.0
 */
public enum ClientPermission implements Permission {
    CLIENT_CREATE,
    CLIENT_READ,
    CLIENT_READ_ALL,
    CLIENT_UPDATE,
    CLIENT_DELETE;

    @Override
    public String getName() {
        return this.name();
    }
}
