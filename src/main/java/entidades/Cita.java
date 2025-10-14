package entidades;

import jakarta.persistence.*;
import lombok.*;
import servicios.CitaException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "CITAS")
@Getter
@ToString(exclude = {"paciente", "medico", "sala"})
@NoArgsConstructor

public class Cita implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @Setter(AccessLevel.NONE)
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Setter(AccessLevel.NONE)
    @Column(name = "costo", nullable = false, precision = 18, scale = 2)
    private BigDecimal costo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Setter
    private EstadoCita estado;

    @Column(length = 2000)
    @Setter
    private String observaciones;

    // CREO CLASED ANIDADA CitaBuilder, facilitando sintaxis de atributos

    public static class CitaBuilder {
        private Paciente paciente;
        private Medico medico;
        private Sala sala;
        private LocalDateTime fechaHora;
        private BigDecimal costo;
        private EstadoCita estado;
        private String observaciones;

        public CitaBuilder paciente(Paciente paciente) {
            this.paciente = paciente;
            return this;
        }

        public CitaBuilder medico(Medico medico) {
            this.medico = medico;
            return this;
        }

        public CitaBuilder sala(Sala sala) {
            this.sala = sala;
            return this;
        }

        public CitaBuilder fechaHora(LocalDateTime fechaHora) {
            this.fechaHora = fechaHora;
            return this;
        }

        public CitaBuilder costo(BigDecimal costo) {
            this.costo = costo;
            return this;
        }

        public CitaBuilder estado(EstadoCita estado) {
            this.estado = estado;
            return this;
        }

        public CitaBuilder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public Cita build() {
            return new Cita(this);
        }
    }

    public static CitaBuilder builder() {
        return new CitaBuilder();
    }

    public void setEstado(EstadoCita estado) {
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones != null ? observaciones : "";
    }
//CONSTRUCTOR CITA con clase anidada
        private Cita(CitaBuilder builder) {
            this.paciente = Objects.requireNonNull(builder.paciente, "El paciente no puede ser nulo");
            this.medico = Objects.requireNonNull(builder.medico, "El médico no puede ser nulo");
            this.sala = Objects.requireNonNull(builder.sala, "La sala no puede ser nula");
            this.fechaHora = Objects.requireNonNull(builder.fechaHora, "La fecha y hora no pueden ser nulas");
            this.costo = Objects.requireNonNull(builder.costo, "El costo no puede ser nulo");
            this.estado = builder.estado != null ? builder.estado : EstadoCita.PROGRAMADA;
            this.observaciones = builder.observaciones != null ? builder.observaciones : "";
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

        private BigDecimal validarCosto(BigDecimal costo) {
            Objects.requireNonNull(costo, "El costo no puede ser nulo");
            if (costo.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El costo no puede ser negativo");
            }
            return costo;
        }
    }
