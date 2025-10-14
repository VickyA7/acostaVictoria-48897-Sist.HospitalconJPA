package entidades;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@ToString(callSuper = true, exclude = {"departamento", "citas"})
@EqualsAndHashCode(callSuper = true, exclude = {"departamento", "citas"})
@SuperBuilder
public class Medico extends Persona implements Serializable {

    private final Matricula matricula;
    private final EspecialidadMedica especialidad;
    private Departamento departamento;

    @Builder.Default
    private final List<Cita> citas = new ArrayList<>();

    // Constructor manual
    public Medico(String nombre, String apellido, String dni, LocalDate fechaNacimiento,
                  TipoSangre tipoSangre, String numeroMatricula, EspecialidadMedica especialidad) {
        super(nombre, apellido, dni, fechaNacimiento, tipoSangre);
        this.matricula = new Matricula(numeroMatricula);
        this.especialidad = Objects.requireNonNull(especialidad, "La especialidad no puede ser nula");
        this.citas = new ArrayList<>();
    }

    public void setDepartamento(Departamento departamento) {
        if (this.departamento != departamento) {
            this.departamento = departamento;
        }
    }

    public void addCita(Cita cita) {
        if (cita != null && !citas.contains(cita)) {
            this.citas.add(cita);
        }
    }

    public List<Cita> getCitas() {
        return Collections.unmodifiableList(new ArrayList<>(citas));
    }
}
