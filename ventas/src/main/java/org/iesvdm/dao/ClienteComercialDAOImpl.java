package org.iesvdm.dao;

import java.sql.Date;
import java.util.List;

import org.iesvdm.modelo.ClienteComercial;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteComercialDAOImpl implements ClienteComercialDAO {

    private final JdbcTemplate jdbcTemplate;

    ClienteComercialDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ClienteComercial> findByClienteId(int clienteId) {
        return jdbcTemplate.query("""
                SELECT cc.cliente_id, cc.comercial_id,
                       CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS comercial_nombre,
                       cc.fecha, cc.prioridad
                FROM cliente_has_comercial cc
                INNER JOIN comercial c ON c.id = cc.comercial_id
                WHERE cc.cliente_id = ?
                ORDER BY cc.prioridad, cc.fecha, comercial_nombre
                """,
                (resultSet, rowNum) -> {
                    Date fecha = resultSet.getDate("fecha");
                    return new ClienteComercial(
                            resultSet.getInt("cliente_id"),
                            resultSet.getInt("comercial_id"),
                            resultSet.getString("comercial_nombre"),
                            fecha == null ? null : fecha.toLocalDate(),
                            resultSet.getInt("prioridad"));
                },
                clienteId);
    }

    @Override
    public void create(ClienteComercial clienteComercial) {
        jdbcTemplate.update("""
                INSERT INTO cliente_has_comercial (cliente_id, comercial_id, fecha, prioridad)
                VALUES (?, ?, ?, ?)
                """,
                clienteComercial.getClienteId(),
                clienteComercial.getComercialId(),
                Date.valueOf(clienteComercial.getFecha()),
                clienteComercial.getPrioridad());
    }

    @Override
    public void delete(int clienteId, int comercialId) {
        jdbcTemplate.update("""
                DELETE FROM cliente_has_comercial
                WHERE cliente_id = ? AND comercial_id = ?
                """,
                clienteId,
                comercialId);
    }
}