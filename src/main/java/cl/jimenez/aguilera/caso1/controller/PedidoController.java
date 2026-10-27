package cl.jimenez.aguilera.caso1.controller;

import cl.jimenez.aguilera.caso1.dto.ActualizarEstadoPedidoDTO;
import cl.jimenez.aguilera.caso1.dto.CrearPedidoDTO;
import cl.jimenez.aguilera.caso1.model.Pedido;
import cl.jimenez.aguilera.caso1.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listar(@RequestParam(name = "rut", required = false) String rut) {
        if (rut != null && !rut.trim().isEmpty()) {
            return pedidoService.listarPorClienteRut(rut.trim());
        }
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<Pedido> obtenerPorNumero(@PathVariable String numeroPedido) {
        return pedidoService.obtenerPorNumeroPedido(numeroPedido)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody CrearPedidoDTO dto) {
        Pedido creado = pedidoService.crearPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoPedidoDTO dto) {
        Pedido actualizado = pedidoService.actualizarEstado(id, dto.getEstado());
        return ResponseEntity.ok(actualizado);
    }
}
