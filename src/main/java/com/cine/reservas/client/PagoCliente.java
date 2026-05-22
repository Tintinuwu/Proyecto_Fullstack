package com.cine.reservas.client;

import com.cine.reservas.dto.PagoRequestDTO;
import com.cine.reservas.dto.PagoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-pagos", url = "http://localhost:8085")
public interface PagoCliente {

    @PostMapping("/api/pagos")
    PagoResponseDTO procesarPago(@RequestBody PagoRequestDTO pagoRequest);
}