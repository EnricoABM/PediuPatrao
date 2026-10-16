package com.umc.pediupatrao.entity.permission;

public enum AuditPermission implements Permission {
    AUDIT_READ_ALL;

    @Override
    public String getName() {
        return this.name();
    }
    
}
