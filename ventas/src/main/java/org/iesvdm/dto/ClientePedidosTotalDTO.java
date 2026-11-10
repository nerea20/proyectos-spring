package org.iesvdm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientePedidosTotalDTO {
    private int clienteId;
    private String nombreCompleto;
    private double importeTotal;
}