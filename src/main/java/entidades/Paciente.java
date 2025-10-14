package entidades;
import lombok.*;
import lombok.experimental.SuperBuilder;
import entidades.enums.TipoSangre;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Getter
@ToString(callSuper = true, exclude = {"hospital", "citas"})
@EqualsAndHashCode(callSuper = true, exclude = {"hospital", "citas"})
@SuperBuilder
public class Paciente extends Persona implements Serializable {

    private final HistoriaClinica historiaClinica;
    private final String telefono;
    private final String direccion;
    private Hospital hospital;

    private final List<Cita> citas;

    public Paciente(String nombre, String apellido, String dni, LocalDate fechaNacimiento,
                    TipoSangre tipoSangre, String telefono, String direccion) {
        super(nombre, apellido, dni, fechaNacimiento, tipoSangre);
        this.telefono = validarString(telefono, "El teléfono no puede ser nulo ni vacío");
        this.direccion = validarString(direccion, "La dirección no puede ser nula ni vacía");
        this.historiaClinica = new HistoriaClinica(this);
        this.citas = new ArrayList<>();
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

