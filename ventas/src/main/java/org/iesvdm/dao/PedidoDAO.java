package org.iesvdm.dao;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dto.ClientePedidosTotalDTO;
import org.iesvdm.dto.EstadisticasPedidosDTO;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;

public interface PedidoDAO {
    void create(Pedido pedido);
    List<Pedido> getAll();
    List<PedidoDetalle> getAllDetalles();
    Optional<Pedido> find(int id);
    Optional<PedidoDetalle> findDetalle(int id);
    List<PedidoDetalle> findByCommercialId(int comercialId);
    EstadisticasPedidosDTO getEstadisticasByCommercialId(int comercialId);
    List<ClientePedidosTotalDTO> findClientesPorImporteTotalByCommercialId(int comercialId);
    List<PedidoDetalle> findByClientId(int clienteId);
    void update(Pedido pedido);
    void delete(int id);
}