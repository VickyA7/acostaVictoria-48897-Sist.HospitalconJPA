package entidades;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="HISTORIA CLINICA")
@Getter
@ToString(exclude = "paciente")
@NoArgsConstructor

public class HistoriaClinica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter(AccessLevel.NONE)
    @Column(name = "numero_historia", nullable = false, unique = true, length = 64)
    private String numeroHistoria;

    @Setter(AccessLevel.NONE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    @Setter(AccessLevel.NONE)
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ElementCollection
    @CollectionTable(name = "hc_diagnostico", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "diagnostico", nullable = false, length = 500)
    private final List<String> diagnosticos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "hc_tratamiento", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "tratamiento", nullable = false, length = 500)
    private final List<String> tratamientos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "hc_alergia", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "alergia", nullable = false, length = 200)
    private final List<String> alergias = new ArrayList<>();



    //BUILDER
    @Builder
    protected HistoriaClinica(Paciente paciente, LocalDateTime fechaCreacion) {
        this.paciente = Objects.requireNonNull(paciente, "El paciente no puede ser nulo");
        this.fechaCreacion = (fechaCreacion != null) ? fechaCreacion : LocalDateTime.now();
        this.numeroHistoria = generarNumeroHistoria();
    }

    private String generarNumeroHistoria() {
        return "HC-" + paciente.getDni() + "-" + fechaCreacion.getYear();
    }

    public void agregarDiagnostico(String diagnostico) {
        if (diagnostico != null && !diagnostico.trim().isEmpty()) {
            diagnosticos.add(diagnostico);
        }
    }

    public void agregarTratamiento(String tratamiento) {
        if (tratamiento != null && !tratamiento.trim().isEmpty()) {
            tratamientos.add(tratamiento);
        }
    }

    public void agregarAlergia(String alergia) {
        if (alergia != null && !alergia.trim().isEmpty()) {
            alergias.add(alergia);
        }
    }

    public List<String> getDiagnosticos() {
        return Collections.unmodifiableList(diagnosticos);
    }

    public List<String> getTratamientos() {
        return Collections.unmodifiableList(tratamientos);
    }

    public List<String> getAlergias() {
        return Collections.unmodifiableList(alergias);
    }

}