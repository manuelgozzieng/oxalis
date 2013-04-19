package eu.peppol.persistence.sql.dao;

import eu.peppol.statistics.CacheWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author steinar
 *         Date: 08.04.13
 *         Time: 11:21
 */
//public abstract class GenericDao<K, V extends BaseEntity> implements Dao<K, V>
public abstract class GenericDao<K, V> implements Dao<K, V>
{
    /* Here is the cache reference */
    protected CacheWrapper<K, V> cache;

    protected GenericDao(CacheWrapper<K, V> cache) {
        this.cache = cache;
    }

    /* the cacheable method */
    public V foreignKeyValueFor(Connection con, final K id) {
        V value;

        if ((value = cache.get(id)) == null) {
            value = findById(con,id);
            if (value == null) {
                value = insert(con, id);
            }
            if (value != null) {
                cache.put(id, value);
            } else if (value == null) {
                throw new IllegalStateException("No value for " + id.toString() + " found in cache or inserted into DBMS");
            }
        }
        return value;
    }

    protected abstract V insert(Connection con, K id);

    protected abstract V findById(Connection con, K id);


    /** Convenience method for preparing a statement alleviating the need to catch exceptions */
    PreparedStatement prepare(Connection con, String sql) {
        try {
            return con.prepareStatement(sql);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to prepare " + sql + "; " + e, e);
        }


    }

    protected void checkConnection(Connection con) {
        if (con == null) {
            throw new IllegalStateException("DBMS connection required");
        }
    }

    protected Integer fetchGeneratedKey(String sql, PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return new Integer(rs.getInt(1));
        } else
            throw new IllegalStateException("Unable to obtain generated primary key for " + sql);
    }

    protected Integer insertAndReturnGeneratedKey(Connection con, String sql, String... params){
        checkConnection(con);

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                ps.setString(i+1, param);
            }
            int rc = ps.executeUpdate();
            return fetchGeneratedKey(sql, ps);

        } catch (SQLException e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(param);
            }

            throw new IllegalStateException("Unable to execute " + sql + " with params " + sb.toString(), e);
        }


    }

    protected Integer selectForeignFor(Connection con, String sql, String param) {
        checkConnection(con);

        PreparedStatement ps = null;

        ps = prepare(con, sql);
        try {
            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Integer(rs.getInt(1));
            } else return null;
        } catch (SQLException e) {
            throw new IllegalStateException("Error during execution of " + sql + ", with param " + param, e);
        }

    }
}