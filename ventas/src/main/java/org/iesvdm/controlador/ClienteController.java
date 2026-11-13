package org.iesvdm.controlador;

import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.ClienteComercial;
import org.iesvdm.modelo.Comercial;

import java.util.ArrayList;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;



@RequestMapping("/clientes")
@Controller
public class ClienteController {

    @Autowired 
    private ClienteService clienteService;

    @Autowired
    private ComercialService comercialService;

    @GetMapping
    public String listar(@RequestParam (required = false) String ciudad, Model model) {
        List<Cliente> clientes = new ArrayList<>();
        if (ciudad != null && !ciudad.isEmpty()) {
            clientes = clienteService.listByCiudad(ciudad);
        } else{
            clientes = clienteService.listAll();
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
        cargarComerciales(cliente.get(), model);
        return "cliente-formulario";
    }

    @PostMapping("/editar/{id}/comerciales")
    public String añadirComercial(@PathVariable int id,
            @RequestParam int comercialId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam int prioridad,
            RedirectAttributes redirectAttributes) {
        Optional<Cliente> cliente = clienteService.find(id);
        Optional<Comercial> comercial = comercialService.find(comercialId);
        if (cliente.isEmpty() || comercial.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el cliente o el comercial.");
            return "redirect:/clientes/editar/" + id;
        }
        if (prioridad < 1) {
            redirectAttributes.addFlashAttribute("mensaje", "La prioridad debe ser mayor que cero.");
            return "redirect:/clientes/editar/" + id;
        }
        if (clienteService.findComercialesByClienteId(id).stream()
                .anyMatch(vinculo -> vinculo.getComercialId() == comercialId)) {
            redirectAttributes.addFlashAttribute("mensaje", "Ese comercial ya está vinculado al cliente.");
            return "redirect:/clientes/editar/" + id;
        }

        clienteService.addComercial(new ClienteComercial(id, comercialId, comercial.get().getNombre(), fecha, prioridad));
        redirectAttributes.addFlashAttribute("mensaje", "Comercial añadido al cliente correctamente.");
        return "redirect:/clientes/editar/" + id;
    }

    @PostMapping("/editar/{id}/comerciales/{comercialId}/eliminar")
    public String eliminarComercial(@PathVariable int id, @PathVariable int comercialId,
            RedirectAttributes redirectAttributes) {
        clienteService.deleteComercial(id, comercialId);
        redirectAttributes.addFlashAttribute("mensaje", "Comercial desvinculado del cliente correctamente.");
        return "redirect:/clientes/editar/" + id;
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

    private void cargarComerciales(Cliente cliente, Model model) {
        List<ClienteComercial> vinculados = clienteService.findComercialesByClienteId(cliente.getId());
        List<Comercial> disponibles = comercialService.listAll().stream()
                .filter(comercial -> vinculados.stream()
                        .noneMatch(vinculo -> vinculo.getComercialId() == comercial.getId()))
                .toList();
        model.addAttribute("comercialesVinculados", vinculados);
        model.addAttribute("comercialesDisponibles", disponibles);
    }

}
