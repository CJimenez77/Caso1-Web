package cl.jimenez.aguilera.caso1.service;

import cl.jimenez.aguilera.caso1.model.Producto;
import cl.jimenez.aguilera.caso1.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return productoRepository.findAllByActivoTrue();
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
}
