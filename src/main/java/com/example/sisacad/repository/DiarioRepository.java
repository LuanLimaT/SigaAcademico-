package com.example.sisacad.repository;

import com.example.sisacad.model.Diario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sisacad.model.Disciplina;
import java.util.List;

@Repository
public interface DiarioRepository extends JpaRepository<Diario, Long> {
    List<Diario> findByDisciplina(Disciplina disciplina);
}
