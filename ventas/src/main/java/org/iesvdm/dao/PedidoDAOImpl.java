package org.iesvdm.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PedidoDAOImpl implements PedidoDAO {

    private final JdbcTemplate jdbcTemplate;

    PedidoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public synchronized void create(Pedido pedido) {
        String sqlInsert = """
                INSERT INTO pedido (total, fecha, id_cliente, id_comercial)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlInsert, new String[] { "id" });
            int parameterIndex = 1;
            statement.setDouble(parameterIndex++, pedido.getTotal());
            statement.setDate(parameterIndex++, toSqlDate(pedido.getFecha()));
            statement.setInt(parameterIndex++, pedido.getClienteId());
            statement.setInt(parameterIndex, pedido.getComercialId());
            return statement;
        }, keyHolder);

        pedido.setId(keyHolder.getKey().intValue());
        log.info("Insertados {} registros.", rows);
    }

    @Override
    public List<Pedido> getAll() {
        List<Pedido> pedidos = jdbcTemplate.query("SELECT * FROM pedido", this::mapPedido);
        log.info("Devueltos {} registros.", pedidos.size());
        return pedidos;
    }

    @Override
    public List<PedidoDetalle> getAllDetalles() {
        String sql = """
                SELECT p.id, p.total, p.fecha, p.id_cliente, p.id_comercial,
                       CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS cliente_nombre,
                       CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS comercial_nombre
                FROM pedido p
                INNER JOIN cliente c ON p.id_cliente = c.id
                INNER JOIN comercial co ON p.id_comercial = co.id
                ORDER BY p.id
                """;

        List<PedidoDetalle> pedidos = jdbcTemplate.query(sql, this::mapPedidoDetalle);
        log.info("Devueltos {} detalles de pedido.", pedidos.size());
        return pedidos;
    }

    @Override
    public Optional<Pedido> find(int id) {
        List<Pedido> pedidos = jdbcTemplate.query(
                "SELECT * FROM pedido WHERE id = ?",
                this::mapPedido,
                id);

        if (pedidos.isEmpty()) {
            log.info("Pedido no encontrado.");
            return Optional.empty();
        }

        return Optional.of(pedidos.get(0));
    }

    @Override
    public Optional<PedidoDetalle> findDetalle(int id) {
        String sql = """
                SELECT p.id, p.total, p.fecha, p.id_cliente, p.id_comercial,
                       CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS cliente_nombre,
                       CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS comercial_nombre
                FROM pedido p
                INNER JOIN cliente c ON p.id_cliente = c.id
                INNER JOIN comercial co ON p.id_comercial = co.id
                WHERE p.id = ?
                """;

        List<PedidoDetalle> pedidos = jdbcTemplate.query(sql, this::mapPedidoDetalle, id);
        if (pedidos.isEmpty()) {
            log.info("Detalle de pedido no encontrado.");
            return Optional.empty();
        }

        return Optional.of(pedidos.get(0));
    }

    @Override
    public void update(Pedido pedido) {
        String sqlUpdate = """
                UPDATE pedido SET
                    total = ?,
                    fecha = ?,
                    id_cliente = ?,
                    id_comercial = ?
                WHERE id = ?
                """;

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlUpdate);
            int parameterIndex = 1;
            statement.setDouble(parameterIndex++, pedido.getTotal());
            statement.setDate(parameterIndex++, toSqlDate(pedido.getFecha()));
            statement.setInt(parameterIndex++, pedido.getClienteId());
            statement.setInt(parameterIndex++, pedido.getComercialId());
            statement.setInt(parameterIndex, pedido.getId());
            return statement;
        });

        log.info("Update de Pedido con {} registros actualizados.", rows);
    }

    @Override
    public void delete(int id) {
        int rows = jdbcTemplate.update("DELETE FROM pedido WHERE id = ?", id);
        log.info("Delete de Pedido con {} registros eliminados.", rows);
    }

    private Pedido mapPedido(ResultSet resultSet, int rowNum) throws SQLException {
        Date fecha = resultSet.getDate("fecha");
        return new Pedido(
                resultSet.getInt("id"),
                resultSet.getDouble("total"),
                fecha == null ? null : fecha.toLocalDate(),
                resultSet.getInt("id_cliente"),
                resultSet.getInt("id_comercial"));
    }

    private PedidoDetalle mapPedidoDetalle(ResultSet resultSet, int rowNum) throws SQLException {
        Date fecha = resultSet.getDate("fecha");
        return new PedidoDetalle(
                resultSet.getInt("id"),
                resultSet.getDouble("total"),
                fecha == null ? null : fecha.toLocalDate(),
                resultSet.getInt("id_cliente"),
                resultSet.getInt("id_comercial"),
                resultSet.getString("cliente_nombre"),
                resultSet.getString("comercial_nombre"));
    }

    private Date toSqlDate(java.time.LocalDate fecha) {
        return fecha == null ? null : Date.valueOf(fecha);
    }
}