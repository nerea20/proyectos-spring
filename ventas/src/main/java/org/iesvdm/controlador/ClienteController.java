package org.iesvdm.controlador;

import org.iesvdm.servicios.ClienteService;
import org.iesvdm.modelo.Cliente;
import java.util.List;
import java.util.Optional;
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
public class ClienteController {

    @Autowired 
    private ClienteService clienteService;

    @GetMapping("/clientes")
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.listAll();
        model.addAttribute("clientes", clientes);
        return "clientes";
    }

    @GetMapping("/clientes/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-crear";
    }

    @PostMapping("/clientes/crear")
    public String crear(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        clienteService.create(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente creado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/clientes/detalle/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> cliente = clienteService.find(id);
        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el cliente.");
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cliente.get());
        return "cliente-detalle";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> cliente = clienteService.find(id);
        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el cliente.");
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cliente.get());
        return "cliente-formulario";
    }

    @PostMapping("/clientes/editar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Cliente cliente,
            RedirectAttributes redirectAttributes) {
        cliente.setId(id);
        clienteService.update(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado correctamente.");
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente eliminado correctamente.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "No se puede eliminar el cliente porque tiene pedidos asociados.");
        }
        return "redirect:/clientes";
    }

}
