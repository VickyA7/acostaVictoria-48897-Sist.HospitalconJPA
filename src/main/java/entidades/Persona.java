package entidades;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@ToString
@SuperBuilder
@MappedSuperclass
public abstract class Persona implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Setter(AccessLevel.NONE)
    @Column(name = "nombre", nullable = false, length = 100)
    protected String nombre;

    @Setter(AccessLevel.NONE)
    @Column(name = "apellido", nullable = false, length = 100)
    protected String apellido;

    @Setter(AccessLevel.NONE)
    @Column(name = "dni", nullable = false, unique = true, length = 8)
    protected String dni;

    @Setter(AccessLevel.NONE)
    @Column(name = "fecha_nacimiento", nullable = false)
    protected LocalDate fechaNacimiento;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sangre", nullable = false)
    protected TipoSangre tipoSangre;

    // Constructor protegido sin parámetros para JPA
    protected Persona() {
        // Constructor vacío para JPA
    }

    protected Persona(PersonaBuilder<?, ?> builder) {
        this.nombre = validarString(builder.nombre, "El nombre no puede ser nulo ni vacío");
        this.apellido = validarString(builder.apellido, "El apellido no puede ser nulo ni vacío");
        this.dni = validarDni(builder.dni);
        this.fechaNacimiento = Objects.requireNonNull(builder.fechaNacimiento, "La fecha de nacimiento no puede ser nula");
        this.tipoSangre = Objects.requireNonNull(builder.tipoSangre, "El tipo de sangre no puede ser nulo");
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public int getEdad() {
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

    private String validarString(String valor, String mensajeError) {
        Objects.requireNonNull(valor, mensajeError);
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor;
    }

    private String validarDni(String dni) {
        Objects.requireNonNull(dni, "El DNI no puede ser nulo");
        if (!dni.matches("\\d{7,8}")) {
            throw new IllegalArgumentException("El DNI debe tener 7 u 8 dígitos");
        }
        return dni;
    }

}