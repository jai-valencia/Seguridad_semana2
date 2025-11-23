package com.duoc.semana2.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duoc.semana2.model.Receta;
import com.duoc.semana2.model.Usuario;
import com.duoc.semana2.service.RecetaService;
import com.duoc.semana2.service.UsuarioService;

import jakarta.persistence.metamodel.Metamodel;
import lombok.RequiredArgsConstructor;

import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController {
    
    
    private RecetaService recetaService;
    
    
    private UsuarioService usuarioService;
    
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioService.obtenerPorUsername(username);
        
        // Obtener las recetas del usuario
        List<Receta> misRecetas = recetaService.obtenerRecetasPorUsuario(usuario.getId());
        
        // Obtener las recetas favoritas
        List<Receta> recetasFavoritas = recetaService.obtenerRecetasFavoritas(usuario.getId());
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("misRecetas", misRecetas);
        model.addAttribute("recetasFavoritas", recetasFavoritas);
        
        return "usuario-dashboard";
    }
    
    @GetMapping("/nueva-receta")
    public String mostrarFormularioNuevaReceta(Model model) {
        model.addAttribute("receta", new Receta());
        return "formulario-receta";
    }
    
    @PostMapping("/guardar-receta")
    public String guardarReceta(@ModelAttribute Receta receta, 
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Usuario usuario = usuarioService.obtenerPorUsername(username);
        
        receta.setUsuario(usuario);
        receta.setFechaCreacion(LocalDateTime.now());
        
        recetaService.guardarReceta(receta);
        
        redirectAttributes.addFlashAttribute("mensaje", "Receta creada exitosamente");
        return "redirect:/usuario/dashboard";
    }
    
    @GetMapping("/editar-receta/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, 
                                          Model model,
                                          Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioService.obtenerPorUsername(username);
        
        Receta receta = recetaService.obtenerPorId(id);
        
        // Verificar que la receta pertenece al usuario
        if (receta == null || !((Usuario) receta.getUsuario()).getId().equals(usuario.getId())) {
            return "redirect:/usuario/dashboard";
        }
        
        model.addAttribute("receta", receta);
        return "formulario-receta";
    }
    
    @DeleteMapping("/eliminar-receta/{id}")
    public String eliminarReceta(@PathVariable Long id, 
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Usuario usuario = usuarioService.obtenerPorUsername(username);
        
        Receta receta = recetaService.obtenerPorId(id);
        
        // Verificar que la receta pertenece al usuario
        if (receta != null && ((Usuario) receta.getUsuario()).getId().equals(usuario.getId())) {
            recetaService.eliminarReceta(id);
            redirectAttributes.addFlashAttribute("mensaje", "Receta eliminada exitosamente");
        }
        
        return "redirect:/usuario/dashboard";
    }
    
    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioActualizado,
                                   @RequestParam(required = false) String nuevaPassword,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        
        usuarioService.actualizarPerfil(username, usuarioActualizado, nuevaPassword);
        
        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado exitosamente");
        return "redirect:/usuario/dashboard";
    }
}