package org.iesvdm.controlador;

import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Comercial;
import org.iesvdm.servicios.ComercialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller 
public class ComercialController {

    @Autowired 
    private ComercialService comercialService;

    @GetMapping("/comerciales")
    public String listar(Model model) {
        List<Comercial> comerciales = comercialService.listAll();
        model.addAttribute("comerciales", comerciales);
        return "comerciales";
    }

    @GetMapping("/comerciales/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("comercial", new Comercial());
        return "comercial-crear";
    }

    @PostMapping("/comerciales/crear")
    public String crear(@ModelAttribute Comercial comercial, RedirectAttributes redirectAttributes) {
        comercialService.create(comercial);
        redirectAttributes.addFlashAttribute("mensaje", "Comercial creado correctamente.");
        return "redirect:/comerciales";
    }

    @GetMapping("/comerciales/detalle/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Comercial> comercial = comercialService.find(id);
        if (comercial.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el comercial.");
            return "redirect:/comerciales";
        }
        model.addAttribute("comercial", comercial.get());
        return "comercial-detalle";
    }

    @GetMapping("/comerciales/editar/{id}")
    public String editar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Comercial> comercial = comercialService.find(id);
        if (comercial.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el comercial.");
            return "redirect:/comerciales";
        }
        model.addAttribute("comercial", comercial.get());
        return "comercial-formulario";
    }

    @PostMapping("/comerciales/editar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Comercial comercial,
            RedirectAttributes redirectAttributes) {
        comercial.setId(id);
        comercialService.update(comercial);
        redirectAttributes.addFlashAttribute("mensaje", "Comercial actualizado correctamente.");
        return "redirect:/comerciales";
    }

    @PostMapping("/comerciales/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            comercialService.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Comercial eliminado correctamente.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "No se puede eliminar el comercial porque tiene pedidos asociados.");
        }
        return "redirect:/comerciales";
    }
    
}
