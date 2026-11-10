package org.iesvdm.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasPedidosDTO {
    private long cantidadPedidos;
    private BigDecimal importeTotal;
    private BigDecimal importeMedio;
    private double importeMaximo;
    private double importeMinimo;
}