package com.example.sisacad.controller;

import com.example.sisacad.model.Disciplina;
import com.example.sisacad.model.Professor;
import com.example.sisacad.repository.ProfessorRepository;
import com.example.sisacad.repository.DisciplinaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @GetMapping
    public String listarProfessores(Model model) {
        model.addAttribute("professores", professorRepository.findAll());
        return "professor/lista-professores";
    }

    @GetMapping("/novo")
    public String mostrarFormularioDeCadastro(Professor professor, Model model) {
        model.addAttribute("todasDisciplinas", disciplinaRepository.findAll());
        return "professor/form-professor";
    }

    @PostMapping("/add")
    public String adicionarProfessor(@Valid Professor professor, BindingResult result, @RequestParam(value = "disciplinaIds", required = false) List<Long> disciplinaIds, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("todasDisciplinas", disciplinaRepository.findAll());
            return "professor/form-professor";
        }

        if (disciplinaIds != null) {
            Set<Disciplina> disciplinas = new HashSet<>(disciplinaRepository.findAllById(disciplinaIds));
            professor.setDisciplinas(disciplinas);
        }

        professorRepository.save(professor);
        return "redirect:/professores";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable("id") long id, Model model) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de professor inválido:" + id));
        model.addAttribute("professor", professor);
        model.addAttribute("todasDisciplinas", disciplinaRepository.findAll());
        return "professor/form-professor";
    }

    @PostMapping("/update/{id}")
    public String atualizarProfessor(@PathVariable("id") long id, @Valid Professor professor, BindingResult result, @RequestParam(value = "disciplinaIds", required = false) List<Long> disciplinaIds, Model model) {
        if (result.hasErrors()) {
            professor.setId(id);
            model.addAttribute("todasDisciplinas", disciplinaRepository.findAll());
            return "professor/form-professor";
        }

        Professor professorExistente = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de professor inválido:" + id));

        professorExistente.setNome(professor.getNome());
        professorExistente.setEmail(professor.getEmail());

        if (disciplinaIds != null) {
            Set<Disciplina> disciplinas = new HashSet<>(disciplinaRepository.findAllById(disciplinaIds));
            professorExistente.setDisciplinas(disciplinas);
        } else {
            professorExistente.setDisciplinas(new HashSet<>()); // Limpa as disciplinas se nenhuma for selecionada
        }

        professorRepository.save(professorExistente);
        return "redirect:/professores";
    }

    @GetMapping("/deletar/{id}")
    public String deletarProfessor(@PathVariable("id") long id, Model model) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de professor inválido:" + id));
        professorRepository.delete(professor);
        return "redirect:/professores";
    }

}
