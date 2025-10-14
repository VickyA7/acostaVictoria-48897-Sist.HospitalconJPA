package entidades;

import lombok.*;
import java.io.Serializable;
import java.util.*;

@Getter
@ToString(exclude = {"departamentos", "pacientes"})
@EqualsAndHashCode(exclude = {"departamentos", "pacientes"})
public class Hospital implements Serializable {

    private final String nombre;
    private final String direccion;
    private final String telefono;

    private final List<Departamento> departamentos = new ArrayList<>();
    private final List<Paciente> pacientes = new ArrayList<>();

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

