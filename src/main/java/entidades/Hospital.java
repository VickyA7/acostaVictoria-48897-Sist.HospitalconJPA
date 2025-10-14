package entidades;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "HOSPITAL")
@Getter
@ToString(exclude = {"departamentos", "pacientes"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hospital implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idHosp")
    private Long id;

    @Setter(AccessLevel.NONE)
    @Column(name="NOMBRE", nullable = false, length = 200)
    private String nombre;

    @Setter(AccessLevel.NONE)
    @Column(name = "DIRECCIÓN", nullable = false, length = 254)
    private String direccion;

    @Setter(AccessLevel.NONE)
    @Column(name = "TELÉFONO", nullable = false, length = 30)
    private String telefono;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "HOSPITAL", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Departamento> departamentos = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "HOSPITAL", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Paciente> pacientes = new ArrayList<>();

    @Builder
    public Hospital(String nombre, String direccion, String telefono) {
        this.nombre = validarString(nombre, "El nombre del hospital no puede ser nulo ni vacío");
        this.direccion = validarString(direccion, "La dirección no puede ser nula ni vacía");
        this.telefono = validarString(telefono, "El teléfono no puede ser nulo ni vacío");
    }

    public void agregarDepartamento(Departamento departamento) {
        if (departamento != null && !departamentos.contains(departamento)) {
            departamentos.add(departamento);
            departamento.setHospital(this);
        }
    }

    public void agregarPaciente(Paciente paciente) {
        if (paciente != null && !pacientes.contains(paciente)) {
            pacientes.add(paciente);
            paciente.setHospital(this);
        }
    }

    public List<Departamento> getDepartamentos() {

        return Collections.unmodifiableList(departamentos);
    }

    public List<Paciente> getPacientes() {

        return Collections.unmodifiableList(pacientes);
    }

    List<Departamento> getInternalDepartamentos() {

        return departamentos;
    }

    List<Paciente> getInternalPacientes() {

        return pacientes;
    }

    private String validarString(String valor, String mensajeError) {
        Objects.requireNonNull(valor, mensajeError);
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor;
    }
}

