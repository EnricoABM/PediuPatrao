package com.umc.pediupatrao.entity.permission;

public enum OrderPermission implements Permission {
    ORDER_CREATE,
    ORDER_READ_ALL,
    ORDER_READ,
    ORDER_DELETE,
    ORDER_PROGRESS,
    ORDER_DESCOUNT;

    @Override
    public String getName() {
        return this.name();
    }

}
