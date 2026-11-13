package org.iesvdm.dao;

import java.util.List;

import org.iesvdm.modelo.ClienteComercial;

public interface ClienteComercialDAO {
    List<ClienteComercial> findByClienteId(int clienteId);
    void create(ClienteComercial clienteComercial);
    void delete(int clienteId, int comercialId);
}