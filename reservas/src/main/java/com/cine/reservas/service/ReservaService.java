package com.cine.reservas.service;

import com.cine.reservas.dto.ReservaRequestDTO;
import com.cine.reservas.dto.ReservaResponseDTO;
import com.cine.reservas.model.Resrvas_model;
import com.cine.reservas.repository.Reserva_repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final Reserva_repository reservaRepository;

    private ReservaResponseDTO mapToDTO(Resrvas_model reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getUsuarioId(),
                reserva.getFuncionId(),
                reserva.getCantidadDeAsientos(),
                reserva.getTotal(),
                reserva.getEstado(),
                reserva.getFechaReserva()
        );
    }

    private Resrvas_model mapToEntity(ReservaRequestDTO dto) {
        Resrvas_model reserva = new Resrvas_model();
        reserva.setUsuarioId(dto.getUsuario_id());
        reserva.setFuncionId(dto.getFuncion_id());
        reserva.setCantidadDeAsientos(dto.getCantidad_de_asientos());
        reserva.setTotal(dto.getTotal());
        reserva.setEstado("PENDIENTE");
        return reserva;
    }

    public List<ReservaResponseDTO> obtenerTodas() {
        return reservaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ReservaResponseDTO> obtenerPorId(Long id) {
        return reservaRepository.findById(id).map(this::mapToDTO);
    }

    public List<ReservaResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> obtenerPorFuncion(Long funcionId) {
        return reservaRepository.findByFuncionId(funcionId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> obtenerPorEstado(String estado) {
        return reservaRepository.findByEstado(estado).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReservaResponseDTO guardar(ReservaRequestDTO dto) {
        Resrvas_model reserva = mapToEntity(dto);
        return mapToDTO(reservaRepository.save(reserva));
    }

    public Optional<ReservaResponseDTO> cancelar(Long id) {
        return reservaRepository.findById(id).map(reserva -> {
            reserva.setEstado("CANCELADA");
            return mapToDTO(reservaRepository.save(reserva));
        });
    }

    public Optional<ReservaResponseDTO> confirmar(Long id) {
        return reservaRepository.findById(id).map(reserva -> {
            reserva.setEstado("CONFIRMADA");
            return mapToDTO(reservaRepository.save(reserva));
        });
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }
}