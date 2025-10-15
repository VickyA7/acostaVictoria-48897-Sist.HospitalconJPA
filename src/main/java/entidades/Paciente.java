package entidades;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "PACIENTES")
@Getter
@ToString(callSuper = true, exclude = {"hospital", "citas"})
@SuperBuilder
@NoArgsConstructor
public class Paciente extends Persona implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private HistoriaClinica historiaClinica;

    @Setter(AccessLevel.NONE)
    @Column(name = "TELEFONO", nullable = false, length = 30)
    private String telefono;

    @Setter(AccessLevel.NONE)
    @Column(name = "DIRECCION", nullable = false, length = 200)
    private String direccion;

    // Hago uso del metodo helper setHospital --> no genero setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "paciente", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private  List<Cita> citas;

    protected Paciente(PacienteBuilder<?, ?> builder) {
        super(builder);
        this.telefono = validarString(builder.telefono, "El teléfono no puede ser nulo ni vacío");
        this.direccion = validarString(builder.direccion, "La dirección no puede ser nula ni vacía");
        this.citas = new ArrayList<>();
        this.historiaClinica = HistoriaClinica.builder()
                .paciente(this)
                .build();
    }

    public static abstract class PacienteBuilder<C extends Paciente, B extends PacienteBuilder<C, B>> extends PersonaBuilder<C, B> {
        private String telefono;
        private String direccion;

        public B telefono(String telefono) {
            this.telefono = telefono;
            return self();
        }

        public B direccion(String direccion) {
            this.direccion = direccion;
            return self();
        }
    }

    public void setHospital(Hospital hospital) {
        if (this.hospital != hospital) {
            if (this.hospital != null) {
                this.hospital.getInternalPacientes().remove(this);
            }
            this.hospital = hospital;
            if (hospital != null) {
                hospital.getInternalPacientes().add(this);
            }
        }
    }

    public void addCita(Cita cita) {

        this.citas.add(cita);
    }

    public List<Cita> getCitas() {

        return Collections.unmodifiableList(new ArrayList<>(citas));
    }

    private String validarString(String valor, String mensajeError) {
        Objects.requireNonNull(valor, mensajeError);
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor;
    }
}

