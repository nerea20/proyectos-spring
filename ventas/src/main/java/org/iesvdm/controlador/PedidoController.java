package org.iesvdm.controlador;

import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.iesvdm.servicios.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/pedidos")
@Controller
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ComercialService comercialService;

    @GetMapping
    public String listar(Model model) {
        List<PedidoDetalle> pedidos = pedidoService.listAllDetalles();
        model.addAttribute("pedidos", pedidos);
        return "pedidos";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("pedido", new Pedido());
        cargarOpcionesFormulario(model);
        return "pedido-crear";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute Pedido pedido, Model model, RedirectAttributes redirectAttributes) {
        if (!relacionesExisten(pedido)) {
            model.addAttribute("error", "El cliente y el comercial seleccionados deben existir.");
            cargarOpcionesFormulario(model);
            return "pedido-crear";
        }

        pedidoService.create(pedido);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido creado correctamente.");
        return "redirect:/pedidos";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PedidoDetalle> pedido = pedidoService.findDetalle(id);
        if (pedido.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el pedido.");
            return "redirect:/pedidos";
        }

        model.addAttribute("pedido", pedido.get());
        return "pedido-detalle";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Pedido> pedido = pedidoService.find(id);
        if (pedido.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se ha encontrado el pedido.");
            return "redirect:/pedidos";
        }

        model.addAttribute("pedido", pedido.get());
        cargarOpcionesFormulario(model);
        return "pedido-formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Pedido pedido,
            Model model, RedirectAttributes redirectAttributes) {
        pedido.setId(id);
        if (!relacionesExisten(pedido)) {
            model.addAttribute("error", "El cliente y el comercial seleccionados deben existir.");
            cargarOpcionesFormulario(model);
            return "pedido-formulario";
        }

        pedidoService.update(pedido);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido actualizado correctamente.");
        return "redirect:/pedidos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        pedidoService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido eliminado correctamente.");
        return "redirect:/pedidos";
    }

    private boolean relacionesExisten(Pedido pedido) {
        boolean clienteExiste = clienteService.listAll().stream()
            .anyMatch(cliente -> cliente.getId() == pedido.getClienteId());
        boolean comercialExiste = comercialService.listAll().stream()
            .anyMatch(comercial -> comercial.getId() == pedido.getComercialId());
        return clienteExiste && comercialExiste;
    }

    private void cargarOpcionesFormulario(Model model) {
        List<Cliente> clientes = clienteService.listAll();
        List<Comercial> comerciales = comercialService.listAll();
        model.addAttribute("clientes", clientes);
        model.addAttribute("comerciales", comerciales);
    }
}