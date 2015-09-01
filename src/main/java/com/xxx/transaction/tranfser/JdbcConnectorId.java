package com.xxx.transaction.tranfser;

import java.util.Objects;

/**
 * Created by ricdong on 15-8-31.
 */
public class JdbcConnectorId {
    private final String id;

    public JdbcConnectorId(String id) {
        this.id = id;
        // TODO should be be null
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj == null || (getClass() != obj.getClass())) {
            return false;
        }

        return Objects.equals(this.id, ((JdbcConnectorId)obj).id);
    }
}
