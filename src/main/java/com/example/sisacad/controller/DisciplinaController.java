package com.example.sisacad.controller;

import com.example.sisacad.model.Aluno;
import com.example.sisacad.model.Diario;
import com.example.sisacad.model.Disciplina;
import com.example.sisacad.repository.DisciplinaRepository;
import com.example.sisacad.repository.AlunoRepository;
import com.example.sisacad.repository.DiarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DiarioRepository diarioRepository;

    @GetMapping
    public String listarDisciplinas(Model model) {
        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        return "disciplina/lista-disciplinas";
    }

    @GetMapping("/novo")
    public String mostrarFormularioDeCadastro(Disciplina disciplina) {
        return "disciplina/form-disciplina";
    }

    @PostMapping("/add")
    public String adicionarDisciplina(@Valid Disciplina disciplina, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "disciplina/form-disciplina";
        }

        disciplinaRepository.save(disciplina);
        return "redirect:/disciplinas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable("id") long id, Model model) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de disciplina inválido:" + id));
        model.addAttribute("disciplina", disciplina);

        // Alunos associados a esta disciplina
        List<Aluno> alunosAssociados = alunoRepository.findAllByDisciplinas_Id(id);
        model.addAttribute("alunosAssociados", alunosAssociados);

        // Diários de turma para esta disciplina
        List<Diario> diariosDaDisciplina = diarioRepository.findByDisciplina(disciplina);
        model.addAttribute("diariosDaDisciplina", diariosDaDisciplina);

        return "disciplina/form-disciplina";
    }

    @PostMapping("/update/{id}")
    public String atualizarDisciplina(@PathVariable("id") long id, @Valid Disciplina disciplina, BindingResult result, Model model) {
        if (result.hasErrors()) {
            disciplina.setId(id);
            return "disciplina/form-disciplina";
        }

        disciplinaRepository.save(disciplina);
        return "redirect:/disciplinas";
    }

    @GetMapping("/deletar/{id}")
    public String deletarDisciplina(@PathVariable("id") long id, Model model) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de disciplina inválido:" + id));
        disciplinaRepository.delete(disciplina);
        return "redirect:/disciplinas";
    }
}
