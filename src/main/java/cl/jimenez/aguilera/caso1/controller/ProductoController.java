package cl.jimenez.aguilera.caso1.controller;

import cl.jimenez.aguilera.caso1.dto.AjustarStockDTO;
import cl.jimenez.aguilera.caso1.model.Producto;
import cl.jimenez.aguilera.caso1.service.ProductoService;
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
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listar(@RequestParam(name = "soloActivos", defaultValue = "true") boolean soloActivos,
                                 @RequestParam(name = "termino", required = false) String termino,
                                 @RequestParam(name = "estado", required = false) String estado) {
        if ((termino != null && !termino.trim().isEmpty()) || (estado != null && !estado.trim().isEmpty())) {
            return productoService.buscarConFiltros(termino, estado);
        }
        return soloActivos ? productoService.listarActivos() : productoService.listarTodos();
    }

    @GetMapping("/bajo-stock")
    public List<Producto> listarBajoStock() {
        return productoService.listarBajoStockMinimo();
    }

    @GetMapping("/criticos")
    public List<Producto> listarCriticos() {
        return productoService.listarEnEstadoCritico();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Producto> obtenerPorCodigo(@PathVariable String codigo) {
        return productoService.obtenerPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        Producto nuevo = productoService.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return productoService.actualizar(id, producto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Producto> ajustarStock(@PathVariable Long id, @Valid @RequestBody AjustarStockDTO dto) {
        Producto actualizado = productoService.ajustarStock(id, dto.getNuevoStock());
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = productoService.eliminar(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
