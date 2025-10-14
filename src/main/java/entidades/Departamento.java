package entidades;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "DEPARTAMENTOS")
@Getter
@ToString(exclude = {"hospital", "medicos", "salas"})

public class Departamento implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @Column(name="NOMBRE", nullable = false, length = 200)
    private  String nombre;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedica especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "DEPARTAMENTOS", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Medico> medicos;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "DEPARTAMENTOS", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Sala> salas;

//CONSTRUCTOR DEPARTAMENTO
    @Builder
    protected Departamento(String nombre, EspecialidadMedica especialidad) {
        this.nombre = validarString(nombre, "El nombre del departamento no puede ser nulo ni vacío");
        this.especialidad = Objects.requireNonNull(especialidad, "La especialidad no puede ser nula");
        this.medicos = new ArrayList<>();
        this.salas = new ArrayList<>();
    }

//METODOS

    public void setHospital(Hospital hospital) {
        if (this.hospital != hospital) {
            if (this.hospital != null) {
                this.hospital.getInternalDepartamentos().remove(this);
            }
            this.hospital = hospital;
            if (hospital != null) {
                hospital.getInternalDepartamentos().add(this);
            }
        }
    }

    public void agregarMedico(Medico medico) {

        if (medico != null && !medicos.contains(medico)) {
            if (!medico.getEspecialidad().equals(this.especialidad)) {
                throw new IllegalArgumentException("Especialidad incompatible con el departamento seleccionado");
            }else {
                medicos.add(medico);
                medico.setDepartamento(this);
            }
        }
    }


//Aplico FactoryMethod implicitamente
    public Sala crearSala(String numero, String tipo) {
        Sala sala = Sala.builder()
                .numero(numero)
                .tipo(tipo)
                .departamento(this)
                .build();
        salas.add(sala);
        return sala;
    }

    public List<Medico> getMedicos() {
        return Collections.unmodifiableList(medicos);
    }

    public List<Sala> getSalas() {
        return Collections.unmodifiableList(salas);
    }


    private String validarString(String valor, String mensajeError) {
        Objects.requireNonNull(valor, mensajeError);
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor;
    }
}