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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@RequestMapping("/clientes")
@Controller
public class ClienteController {

    @Autowired 
    private ClienteService clienteService;

    @GetMapping
    public String listar(@RequestParam (required = false) String ciudad, Model model) {
        List<Cliente> clientes = clienteService.listAll();
        if (ciudad != null && !ciudad.isEmpty()) {
            clientes = clienteService.listByCiudad(ciudad);
        }
        model.addAttribute("clientes", clientes);
        model.addAttribute("ciudad", ciudad);
        return "clientes";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-crear";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        clienteService.create(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente creado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> cliente = clienteService.find(id);
        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el cliente.");
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cliente.get());
        model.addAttribute("pedidos", clienteService.findPedidosByClientId(id));
        return "cliente-detalle";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> cliente = clienteService.find(id);
        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el cliente.");
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cliente.get());
        return "cliente-formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Cliente cliente,
            RedirectAttributes redirectAttributes) {
        cliente.setId(id);
        clienteService.update(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado correctamente.");
        return "redirect:/clientes";
    }

    @PostMapping("/eliminar/{id}")
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
