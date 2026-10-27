package cl.jimenez.aguilera.caso1.controller;

import cl.jimenez.aguilera.caso1.model.Cliente;
import cl.jimenez.aguilera.caso1.model.EstadoPedido;
import cl.jimenez.aguilera.caso1.model.Pedido;
import cl.jimenez.aguilera.caso1.model.Producto;
import cl.jimenez.aguilera.caso1.service.ClienteService;
import cl.jimenez.aguilera.caso1.service.PedidoService;
import cl.jimenez.aguilera.caso1.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class WebController {

    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    public WebController(ProductoService productoService,
                         ClienteService clienteService,
                         PedidoService pedidoService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/productos";
    }

    @GetMapping("/productos")
    public String catalogoProductos(@RequestParam(name = "termino", required = false) String termino,
                                    @RequestParam(name = "estado", required = false) String estado,
                                    Model model) {
        List<Producto> productos = productoService.buscarConFiltros(termino, estado);

        model.addAttribute("productos", productos);
        model.addAttribute("totalRegistrados", productoService.contarTotalRegistrados());
        model.addAttribute("totalBajoMinimo", productoService.contarBajoStockMinimo());
        model.addAttribute("totalCritico", productoService.contarEnEstadoCritico());
        model.addAttribute("termino", termino);
        model.addAttribute("estado", estado != null ? estado : "todos");

        return "productos/catalogo";
    }

    @PostMapping("/productos/nuevo")
    public String crearProducto(Producto producto) {
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @PostMapping("/productos/{id}/editar")
    public String editarProducto(@PathVariable Long id, Producto producto) {
        productoService.actualizar(id, producto);
        return "redirect:/productos";
    }

    @PostMapping("/productos/{id}/ajustar-stock")
    public String ajustarStockProducto(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        productoService.ajustarStock(id, nuevoStock);
        return "redirect:/productos";
    }

    @GetMapping("/pedidos/nuevo")
    public String nuevoPedido(Model model) {
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
        return "pedidos/nuevo";
    }

    @GetMapping("/pedidos/historial")
    public String historialPedidos(@RequestParam(name = "rut", required = false) String rut,
                                   @RequestParam(name = "tab", required = false, defaultValue = "todos") String tab,
                                   Model model) {
        String rutABuscar = rut;
        if (rutABuscar == null || rutABuscar.trim().isEmpty()) {
            List<Cliente> clientesActivos = clienteService.listarActivos();
            if (!clientesActivos.isEmpty()) {
                rutABuscar = clientesActivos.get(0).getRut();
            }
        }

        Optional<Cliente> clienteOpt = clienteService.buscarPorRutOId(rutABuscar);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            List<Pedido> todos = pedidoService.listarPorCliente(cliente.getId());

            long conteoTodos = todos.size();
            long conteoPendiente = todos.stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).count();
            long conteoCompletado = todos.stream().filter(p -> p.getEstado() == EstadoPedido.COMPLETADO).count();
            long conteoOtros = todos.stream().filter(p -> p.getEstado() == EstadoPedido.ENVIADO || p.getEstado() == EstadoPedido.CANCELADO).count();

            List<Pedido> pedidosFiltrados;
            if ("pendiente".equalsIgnoreCase(tab)) {
                pedidosFiltrados = todos.stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).collect(Collectors.toList());
            } else if ("completado".equalsIgnoreCase(tab)) {
                pedidosFiltrados = todos.stream().filter(p -> p.getEstado() == EstadoPedido.COMPLETADO).collect(Collectors.toList());
            } else if ("otros".equalsIgnoreCase(tab)) {
                pedidosFiltrados = todos.stream().filter(p -> p.getEstado() == EstadoPedido.ENVIADO || p.getEstado() == EstadoPedido.CANCELADO).collect(Collectors.toList());
            } else {
                pedidosFiltrados = todos;
            }

            model.addAttribute("cliente", cliente);
            model.addAttribute("rut", cliente.getRut());
            model.addAttribute("tab", tab);
            model.addAttribute("pedidos", pedidosFiltrados);
            model.addAttribute("totalPedidos", conteoTodos);
            model.addAttribute("conteoTodos", conteoTodos);
            model.addAttribute("conteoPendiente", conteoPendiente);
            model.addAttribute("conteoCompletado", conteoCompletado);
            model.addAttribute("conteoOtros", conteoOtros);
        } else {
            model.addAttribute("cliente", null);
            model.addAttribute("rut", rut);
            model.addAttribute("tab", tab);
            model.addAttribute("pedidos", List.of());
            model.addAttribute("totalPedidos", 0);
            model.addAttribute("conteoTodos", 0);
            model.addAttribute("conteoPendiente", 0);
            model.addAttribute("conteoCompletado", 0);
            model.addAttribute("conteoOtros", 0);
        }

        return "pedidos/historial";
    }
}
