package cl.jimenez.aguilera.caso1.repository;

import cl.jimenez.aguilera.caso1.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findAllByActivoTrue();

    List<Producto> findAllByActivoTrueOrderByCodigoAsc();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockDisponible <= p.stockMinimo ORDER BY p.codigo ASC")
    List<Producto> findBajoStockMinimo();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockDisponible <= 5 ORDER BY p.codigo ASC")
    List<Producto> findEnEstadoCritico();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (LOWER(p.codigo) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))) ORDER BY p.codigo ASC")
    List<Producto> buscarPorCodigoONombre(@Param("termino") String termino);

    long countByActivoTrue();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true AND p.stockDisponible <= p.stockMinimo")
    long countBajoStockMinimo();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true AND p.stockDisponible <= 5")
    long countEnEstadoCritico();
}
