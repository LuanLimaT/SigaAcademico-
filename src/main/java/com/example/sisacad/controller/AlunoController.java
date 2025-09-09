package com.example.sisacad.controller;

import com.example.sisacad.model.Aluno;
import com.example.sisacad.model.Disciplina;
import com.example.sisacad.repository.AlunoRepository;
import com.example.sisacad.repository.DisciplinaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @GetMapping
    public String listarAlunos(@RequestParam(required = false) String turno, Model model) {
        List<Aluno> alunos;
        if (turno != null && !turno.isBlank()) {
            alunos = alunoRepository.findAllByTurno(turno);
        } else {
            alunos = alunoRepository.findAll();
        }
        model.addAttribute("alunos", alunos);
        model.addAttribute("turnos", alunoRepository.findDistinctTurnos()); 
        return "aluno/lista-alunos";
    }

    @GetMapping("/novo")
    public String mostrarFormularioDeCadastro(Aluno aluno, Model model) {
        model.addAttribute("todasDisciplinas", disciplinaRepository.findAll());
        return "aluno/form-aluno";
    }

    @PostMapping("/add")
    public String adicionarAluno(@Valid Aluno aluno, BindingResult result, @RequestParam(value = "disciplinaIds", required = false) List<Long> disciplinaIds, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("todasDisciplinas", disciplinaRepository.findAll());
            return "aluno/form-aluno";
        }

        if (disciplinaIds != null) {
            Set<Disciplina> disciplinas = new HashSet<>(disciplinaRepository.findAllById(disciplinaIds));
            aluno.setDisciplinas(disciplinas);
        }

        alunoRepository.save(aluno);
        return "redirect:/alunos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable("id") long id, Model model) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de aluno inválido:" + id));
        model.addAttribute("aluno", aluno);
        model.addAttribute("todasDisciplinas", disciplinaRepository.findAll());
        return "aluno/form-aluno";
    }

    @PostMapping("/update/{id}")
    public String atualizarAluno(@PathVariable("id") long id, @Valid Aluno aluno, BindingResult result, @RequestParam(value = "disciplinaIds", required = false) List<Long> disciplinaIds, Model model) {
        if (result.hasErrors()) {
            aluno.setId(id);
            model.addAttribute("todasDisciplinas", disciplinaRepository.findAll());
            return "aluno/form-aluno";
        }

        Aluno alunoExistente = alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de aluno inválido:" + id));

        alunoExistente.setNome(aluno.getNome());
        alunoExistente.setMatricula(aluno.getMatricula());
        alunoExistente.setTurno(aluno.getTurno());
        alunoExistente.setEmail(aluno.getEmail());

        if (disciplinaIds != null) {
            Set<Disciplina> disciplinas = new HashSet<>(disciplinaRepository.findAllById(disciplinaIds));
            alunoExistente.setDisciplinas(disciplinas);
        } else {
            alunoExistente.setDisciplinas(new HashSet<>()); // Limpa as disciplinas se nenhuma for selecionada
        }

        alunoRepository.save(alunoExistente);
        return "redirect:/alunos";
    }

    @GetMapping("/deletar/{id}")
    public String deletarAluno(@PathVariable("id") long id, Model model) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de aluno inválido:" + id));
        alunoRepository.delete(aluno);
        return "redirect:/alunos";
    }

    @GetMapping("/pesquisar")
    public String pesquisarAluno(@RequestParam("matricula") String matricula, Model model) {
        if (matricula.isBlank()) {
            return "redirect:/alunos";
        }

        alunoRepository.findByMatricula(matricula).ifPresent(aluno -> model.addAttribute("alunos", aluno));
        return "aluno/lista-alunos";
    }

    @GetMapping("/api/alunos/por-disciplina")
    @ResponseBody
    public List<Aluno> getAlunosPorDisciplina(@RequestParam Long disciplinaId) {
        return alunoRepository.findAllByDisciplinas_Id(disciplinaId);
    }

}
