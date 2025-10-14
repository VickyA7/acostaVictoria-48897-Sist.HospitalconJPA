package entidades;

import lombok.*;
import org.jcr.Entidades.Enums.EstadoCita;
import org.jcr.Entidades.Exceptions.CitaException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Getter
@ToString(exclude = {"paciente", "medico", "sala"})
@EqualsAndHashCode(of = {"paciente", "medico", "fechaHora"}) // clave natural
@Builder
public class Cita implements Serializable {

    private final Paciente paciente;
    private final Medico medico;
    private final Sala sala;
    private final LocalDateTime fechaHora;
    private final BigDecimal costo;

    @Setter
    @Builder.Default
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Setter
    @Builder.Default
    private String observaciones = "";

    // 🔒 Validaciones centralizadas
    @Builder
    private Cita(Paciente paciente,
                 Medico medico,
                 Sala sala,
                 LocalDateTime fechaHora,
                 BigDecimal costo,
                 EstadoCita estado,
                 String observaciones) {

        this.paciente = Objects.requireNonNull(paciente, "El paciente no puede ser nulo");
        this.medico = Objects.requireNonNull(medico, "El médico no puede ser nulo");
        this.sala = Objects.requireNonNull(sala, "La sala no puede ser nula");
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora no pueden ser nulas");
        this.costo = validarCosto(costo);

        this.estado = (estado != null) ? estado : EstadoCita.PROGRAMADA;
        this.observaciones = (observaciones != null) ? observaciones : "";
    }

    private BigDecimal validarCosto(BigDecimal costo) {
        Objects.requireNonNull(costo, "El costo no puede ser nulo");
        if (costo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }
        return costo;
    }

    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                paciente.getDni(),
                medico.getDni(),
                sala.getNumero(),
                fechaHora,
                costo,
                estado.name(),
                observaciones.replaceAll(",", ";"));
    }

    public static Cita fromCsvString(String csvString,
                                     Map<String, Paciente> pacientes,
                                     Map<String, Medico> medicos,
                                     Map<String, Sala> salas) throws CitaException {
        String[] values = csvString.split(",");
        if (values.length != 7) {
            throw new CitaException("Formato de CSV inválido para Cita: " + csvString);
        }

        String dniPaciente = values[0];
        String dniMedico = values[1];
        String numeroSala = values[2];
        LocalDateTime fechaHora = LocalDateTime.parse(values[3]);
        BigDecimal costo = new BigDecimal(values[4]);
        EstadoCita estado = EstadoCita.valueOf(values[5]);
        String observaciones = values[6].replaceAll(";", ",");

        Paciente paciente = pacientes.get(dniPaciente);
        Medico medico = medicos.get(dniMedico);
        Sala sala = salas.get(numeroSala);

        if (paciente == null) throw new CitaException("Paciente no encontrado: " + dniPaciente);
        if (medico == null) throw new CitaException("Médico no encontrado: " + dniMedico);
        if (sala == null) throw new CitaException("Sala no encontrada: " + numeroSala);

        // 🔹 Validaciones igual que en programarCita
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new CitaException("No se puede cargar una cita con fecha en el pasado: " + fechaHora);
        }
        if (costo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CitaException("El costo debe ser mayor que cero. Valor: " + costo);
        }
        if (!medico.getEspecialidad().equals(sala.getDepartamento().getEspecialidad())) {
            throw new CitaException("La especialidad del médico no coincide con el departamento de la sala.");
        }

        // Construcción segura de la cita
        Cita cita = Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .sala(sala)
                .fechaHora(fechaHora)
                .costo(costo)
                .build();

        cita.setEstado(estado);
        cita.setObservaciones(observaciones);

        return cita;
    }
}