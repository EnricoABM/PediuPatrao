package com.umc.pediupatrao.entity.permission;

public enum ProductPermission implements Permission {
    PRODUCT_CREATE,
    PRODUCT_READ_ALL,
    PRODUCT_READ,
    PRODUCT_UPDATE,
    PRODUCT_DELETE;

    @Override
    public String getName() {
        return this.name();
    }

}
