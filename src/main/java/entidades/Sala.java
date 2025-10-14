package entidades;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@ToString(exclude = {"citas", "departamento"}) // evitamos recursividad
@EqualsAndHashCode(of = {"numero", "departamento"}) // clave natural
public class Sala implements Serializable {

    private final String numero;
    private final String tipo;
    private final Departamento departamento;

    private final List<Cita> citas;

    // Constructor manual
    public Sala(String numero, String tipo, Departamento departamento) {
        this.numero = validarString(numero, "El número de sala no puede ser nulo ni vacío");
        this.tipo = validarString(tipo, "El tipo de sala no puede ser nulo ni vacío");
        this.departamento = Objects.requireNonNull(departamento, "El departamento no puede ser nulo");
        this.citas = new ArrayList<>(); // inicializamos la lista
    }

    public void addCita(Cita cita) {
        if (cita != null && !citas.contains(cita)) {
            this.citas.add(cita);
        }
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
