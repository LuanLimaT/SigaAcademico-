package com.example.sisacad.repository;

import com.example.sisacad.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    // Método para pesquisar aluno por matrícula
    Optional<Aluno> findByMatricula(String matricula);

    // Método para pesquisar aluno por turno
    List<Aluno> findAllByTurno(String turno);

    @Query("SELECT DISTINCT a.turno FROM Aluno a ORDER BY a.turno")
    List<String> findDistinctTurnos();

    List<Aluno> findAllByDisciplinas_Id(Long disciplinaId);


}
