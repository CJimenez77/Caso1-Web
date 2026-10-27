package cl.jimenez.aguilera.caso1.controller;

import cl.jimenez.aguilera.caso1.dto.ActualizarEstadoClienteDTO;
import cl.jimenez.aguilera.caso1.model.Cliente;
import cl.jimenez.aguilera.caso1.model.Pedido;
import cl.jimenez.aguilera.caso1.service.ClienteService;
import cl.jimenez.aguilera.caso1.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    public ClienteController(ClienteService clienteService, PedidoService pedidoService) {
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Cliente> listar(@RequestParam(name = "soloActivos", defaultValue = "true") boolean soloActivos) {
        return soloActivos ? clienteService.listarActivos() : clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Cliente> obtenerPorRut(@PathVariable String rut) {
        return clienteService.obtenerPorRut(rut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<Pedido>> listarPedidosPorCliente(@PathVariable Long id) {
        if (clienteService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Pedido> pedidos = pedidoService.listarPorCliente(id);
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return clienteService.actualizar(id, cliente)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Cliente> actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoClienteDTO dto) {
        return clienteService.obtenerPorId(id).map(cliente -> {
            cliente.setEstado(dto.getEstado());
            if ("INACTIVO".equalsIgnoreCase(dto.getEstado())) {
                cliente.setActivo(false);
            } else if ("ACTIVO".equalsIgnoreCase(dto.getEstado())) {
                cliente.setActivo(true);
            }
            Cliente guardado = clienteService.guardar(cliente);
            return ResponseEntity.ok(guardado);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = clienteService.eliminar(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
