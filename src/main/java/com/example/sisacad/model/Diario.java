package com.example.sisacad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "diarios")
public class Diario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Column(nullable = false)
    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    @NotBlank(message = "O conteúdo abordado é obrigatório")
    @Size(min = 10, message = "O conteúdo deve ter pelo menos 10 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudoAbordado;

    @OneToMany(mappedBy = "diario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Presenca> presencas = new HashSet<>();

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getConteudoAbordado() {
        return conteudoAbordado;
    }

    public void setConteudoAbordado(String conteudoAbordado) {
        this.conteudoAbordado = conteudoAbordado;
    }

    public Set<Presenca> getPresencas() {
        return presencas;
    }

    public void setPresencas(Set<Presenca> presencas) {
        this.presencas = presencas;
    }
}
