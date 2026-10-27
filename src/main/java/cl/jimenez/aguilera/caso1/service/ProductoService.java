package cl.jimenez.aguilera.caso1.service;

import cl.jimenez.aguilera.caso1.exception.RecursoNoEncontradoException;
import cl.jimenez.aguilera.caso1.model.Producto;
import cl.jimenez.aguilera.caso1.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepository.findAllByActivoTrueOrderByCodigoAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    public Producto guardar(Producto producto) {
        if (producto.getStockDisponible() == null) {
            producto.setStockDisponible(0);
        }
        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(0);
        }
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        return productoRepository.save(producto);
    }

    public Optional<Producto> actualizar(Long id, Producto datosActualizados) {
        return productoRepository.findById(id).map(productoExistente -> {
            productoExistente.setCodigo(datosActualizados.getCodigo());
            productoExistente.setNombre(datosActualizados.getNombre());
            productoExistente.setDescripcion(datosActualizados.getDescripcion());
            productoExistente.setPrecio(datosActualizados.getPrecio());
            productoExistente.setStockDisponible(datosActualizados.getStockDisponible());
            productoExistente.setStockMinimo(datosActualizados.getStockMinimo());
            if (datosActualizados.getActivo() != null) {
                productoExistente.setActivo(datosActualizados.getActivo());
            }
            return productoRepository.save(productoExistente);
        });
    }

    public boolean eliminar(Long id) {
        return productoRepository.findById(id).map(producto -> {
            producto.setActivo(false);
            productoRepository.save(producto);
            return true;
        }).orElse(false);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarBajoStockMinimo() {
        return productoRepository.findBajoStockMinimo();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarEnEstadoCritico() {
        return productoRepository.findEnEstadoCritico();
    }

    public Producto ajustarStock(Long id, Integer nuevoStock) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con identificador: " + id));
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        producto.setStockDisponible(nuevoStock);
        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarConFiltros(String termino, String estadoFiltro) {
        List<Producto> lista;
        if (termino != null && !termino.trim().isEmpty()) {
            lista = productoRepository.buscarPorCodigoONombre(termino.trim());
        } else {
            lista = productoRepository.findAllByActivoTrueOrderByCodigoAsc();
        }

        if (estadoFiltro == null || estadoFiltro.equalsIgnoreCase("todos") || estadoFiltro.trim().isEmpty()) {
            return lista;
        }

        return lista.stream().filter(p -> {
            boolean esCritico = p.getStockDisponible() != null && p.getStockDisponible() <= 5;
            boolean esBajoMinimo = p.estaBajoStockMinimo() && !esCritico;
            boolean esNormal = !p.estaBajoStockMinimo() && !esCritico;

            switch (estadoFiltro.toLowerCase()) {
                case "critico":
                    return esCritico;
                case "bajo_minimo":
                case "bajominimo":
                    return esBajoMinimo;
                case "normal":
                    return esNormal;
                default:
                    return true;
            }
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long contarTotalRegistrados() {
        return productoRepository.countByActivoTrue();
    }

    @Transactional(readOnly = true)
    public long contarBajoStockMinimo() {
        return productoRepository.countBajoStockMinimo();
    }

    @Transactional(readOnly = true)
    public long contarEnEstadoCritico() {
        return productoRepository.countEnEstadoCritico();
    }
}
