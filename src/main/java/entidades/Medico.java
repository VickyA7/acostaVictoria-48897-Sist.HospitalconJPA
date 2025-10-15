package entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "MEDICOS")
@Getter
@ToString(callSuper = true, exclude = {"departamento", "citas"})
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medico extends Persona implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @Embedded
    private Matricula matricula;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedica especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "medico", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Cita> citas;


    protected Medico(MedicoBuilder<?, ?> builder) {
        super(builder);
        this.matricula = new Matricula(builder.numeroMatricula);
        this.especialidad = Objects.requireNonNull(builder.especialidad, "La especialidad no puede ser nula");
        this.citas = new ArrayList<>();
    }

    public static abstract class MedicoBuilder<C extends Medico, B extends MedicoBuilder<C, B>> extends PersonaBuilder<C, B> {
        private String numeroMatricula;
        private EspecialidadMedica especialidad;

        public B numeroMatricula(String numeroMatricula) {
            this.numeroMatricula = numeroMatricula;
            return self();
        }

        public B especialidad(EspecialidadMedica especialidad) {
            this.especialidad = especialidad;
            return self();
        }
    }

    public void setDepartamento(Departamento departamento) {
        if (this.departamento != departamento) {
            this.departamento = departamento;
        }
    }

    public void addCita(Cita cita) {
        this.citas.add(cita);
    }

    public List<Cita> getCitas() {
        return Collections.unmodifiableList(new ArrayList<>(citas));
    }

}