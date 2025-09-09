package com.example.sisacad.controller;

import com.example.sisacad.model.Aluno;
import com.example.sisacad.repository.AlunoRepository;
import com.example.sisacad.repository.PresencaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/presencas")
public class PresencaController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private PresencaRepository presencaRepository;

    @GetMapping
    public String mostrarFormularioDePresenca(Model model) {
        List<Aluno> alunos = alunoRepository.findAll();
        model.addAttribute("alunos", alunos);
        return "presenca/form-presenca";
    }

    // O método para salvar a presença será adicionado aqui

    @PostMapping("/salvar")
    public String salvarPresencas(@RequestParam(value = "presentes", required = false) List<Long> presentes) {
        List<Aluno> todosAlunos = alunoRepository.findAll();
        List<com.example.sisacad.model.Presenca> presencasParaSalvar = new ArrayList<>();

        // LocalDate hoje = LocalDate.now(); // Data agora vem do Diário

        for (Aluno aluno : todosAlunos) {
            com.example.sisacad.model.Presenca presenca = new com.example.sisacad.model.Presenca();
            // presenca.setDiario(diarioSalvo); // Diário será setado no DiarioController
            presenca.setAluno(aluno);
            // presenca.setData(hoje); // Data agora vem do Diário
            presenca.setPresente(presentes != null && presentes.contains(aluno.getId()));
            presencasParaSalvar.add(presenca);
        }

        presencaRepository.saveAll(presencasParaSalvar);

        return "redirect:/?presencaSava=true";
    }
}
