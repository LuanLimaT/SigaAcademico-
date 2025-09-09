package com.example.sisacad.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.sisacad.repository.AlunoRepository;
import com.example.sisacad.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalAlunos", alunoRepository.count());
        model.addAttribute("totalProfessores", professorRepository.count());
        // Adicionar mais estatísticas aqui, como alunos por turno
        return "index";
    }
}
