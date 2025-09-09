package com.example.sisacad.controller;

import com.example.sisacad.model.Diario;
import com.example.sisacad.model.Disciplina;
import com.example.sisacad.model.Professor;
import com.example.sisacad.model.Aluno;
import com.example.sisacad.model.Presenca;
import com.example.sisacad.repository.DiarioRepository;
import com.example.sisacad.repository.DisciplinaRepository;
import com.example.sisacad.repository.ProfessorRepository;
import com.example.sisacad.repository.AlunoRepository;
import com.example.sisacad.repository.PresencaRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/diario")
public class DiarioController {

    @Autowired
    private DiarioRepository diarioRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private PresencaRepository presencaRepository;

    @GetMapping
    public String listarDiarios(Model model) {
        model.addAttribute("diarios", diarioRepository.findAll());
        return "diario/lista-diarios";
    }

    @GetMapping("/novo")
    public String mostrarFormularioDeCadastro(Diario diario, Model model) {
        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
        return "diario/form-diario";
    }

    @PostMapping("/add")
    public String adicionarDiario(@Valid Diario diario, BindingResult result,
                                  @RequestParam("disciplinaId") Long disciplinaId,
                                  @RequestParam("professorId") Long professorId,
                                  @RequestParam(value = "alunosPresentes", required = false) List<Long> alunosPresentes,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("disciplinas", disciplinaRepository.findAll());
            model.addAttribute("professores", professorRepository.findAll());
            return "diario/form-diario";
        }

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina inválida:" + disciplinaId));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor inválido:" + professorId));

        diario.setDisciplina(disciplina);
        diario.setProfessor(professor);
        diario.setData(LocalDate.now()); // Data atual para o diário

        Diario diarioSalvo = diarioRepository.save(diario);

        // Salvar presenças
        if (alunosPresentes != null && !alunosPresentes.isEmpty()) {
            List<Aluno> alunosDaDisciplina = alunoRepository.findAllByDisciplinas_Id(disciplinaId);
            for (Aluno aluno : alunosDaDisciplina) {
                Presenca presenca = new Presenca();
                presenca.setDiario(diarioSalvo);
                presenca.setAluno(aluno);
                presenca.setPresente(alunosPresentes.contains(aluno.getId()));
                presencaRepository.save(presenca);
            }
        }

        return "redirect:/diario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable("id") long id, Model model) {
        Diario diario = diarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de diário inválido:" + id));
        model.addAttribute("diario", diario);
        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());

        // Carregar alunos da disciplina para o formulário de presença
        List<Aluno> alunosDaDisciplina = alunoRepository.findAllByDisciplinas_Id(diario.getDisciplina().getId());
        model.addAttribute("alunosDaDisciplina", alunosDaDisciplina);

        // Carregar presenças existentes para pré-selecionar checkboxes
        Set<Long> presentesIds = new HashSet<>();
        presencaRepository.findByDiario(diario).forEach(p -> {
            if (p.isPresente()) {
                presentesIds.add(p.getAluno().getId());
            }
        });
        model.addAttribute("presentesIds", presentesIds);

        return "diario/form-diario";
    }

    @PostMapping("/update/{id}")
    public String atualizarDiario(@PathVariable("id") long id, @Valid Diario diario, BindingResult result,
                                  @RequestParam("disciplinaId") Long disciplinaId,
                                  @RequestParam("professorId") Long professorId,
                                  @RequestParam(value = "alunosPresentes", required = false) List<Long> alunosPresentes,
                                  Model model) {
        if (result.hasErrors()) {
            diario.setId(id);
            model.addAttribute("disciplinas", disciplinaRepository.findAll());
            model.addAttribute("professores", professorRepository.findAll());
            return "diario/form-diario";
        }

        Diario diarioExistente = diarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de diário inválido:" + id));

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina inválida:" + disciplinaId));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor inválido:" + professorId));

        diarioExistente.setDisciplina(disciplina);
        diarioExistente.setProfessor(professor);
        diarioExistente.setData(diario.getData()); // Manter a data original ou permitir edição?
        diarioExistente.setConteudoAbordado(diario.getConteudoAbordado());

        // Atualizar presenças
        // Primeiro, remover as presenças antigas associadas a este diário
        presencaRepository.deleteAll(presencaRepository.findByDiario(diarioExistente));

        // Depois, salvar as novas presenças
        if (alunosPresentes != null && !alunosPresentes.isEmpty()) {
            List<Aluno> alunosDaDisciplina = alunoRepository.findAllByDisciplinas_Id(disciplinaId);
            for (Aluno aluno : alunosDaDisciplina) {
                Presenca presenca = new Presenca();
                presenca.setDiario(diarioExistente);
                presenca.setAluno(aluno);
                presenca.setPresente(alunosPresentes.contains(aluno.getId()));
                presencaRepository.save(presenca);
            }
        }

        diarioRepository.save(diarioExistente);
        return "redirect:/diario";
    }

    @GetMapping("/deletar/{id}")
    public String deletarDiario(@PathVariable("id") long id, Model model) {
        Diario diario = diarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de diário inválido:" + id));
        diarioRepository.delete(diario);
        return "redirect:/diario";
    }
}
