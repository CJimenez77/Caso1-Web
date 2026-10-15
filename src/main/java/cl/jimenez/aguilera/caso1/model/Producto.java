package cl.jimenez.aguilera.caso1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product code/SKU is mandatory")
    @Size(max = 50, message = "Product code cannot exceed 50 characters")
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "Product name is mandatory")
    @Size(max = 150, message = "Product name cannot exceed 150 characters")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "Unit price is mandatory")
    @PositiveOrZero(message = "Price must be non-negative")
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "Available stock is mandatory")
    @Min(value = 0, message = "Available stock cannot be negative")
    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible = 0;

    @NotNull(message = "Minimum alert stock is mandatory")
    @Min(value = 0, message = "Minimum stock cannot be negative")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Producto() {
    }

    public Producto(String codigo, String nombre, String descripcion, BigDecimal precio, Integer stockDisponible, Integer stockMinimo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stockDisponible = stockDisponible != null ? stockDisponible : 0;
        this.stockMinimo = stockMinimo != null ? stockMinimo : 0;
        this.activo = true;
        this.version = 0L;
    }

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        if (this.stockDisponible == null) {
            this.stockDisponible = 0;
        }
        if (this.stockMinimo == null) {
            this.stockMinimo = 0;
        }
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.version == null) {
            this.version = 0L;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }


    public boolean tieneStockSuficiente(int cantidadSolicitada) {
        return this.stockDisponible != null && this.stockDisponible >= cantidadSolicitada;
    }

    public void descontarStock(int cantidad) {
        if (!tieneStockSuficiente(cantidad)) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + this.nombre +
                    " (Solicitado: " + cantidad + ", Disponible: " + this.stockDisponible + ")");
        }
        this.stockDisponible -= cantidad;
    }

    public void restaurarStock(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a restaurar no puede ser negativa");
        }
        this.stockDisponible += cantidad;
    }

    public boolean estaBajoStockMinimo() {
        return this.stockDisponible != null && this.stockMinimo != null && this.stockDisponible <= this.stockMinimo;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(id, producto.id) || (codigo != null && Objects.equals(codigo, producto.codigo));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codigo);
    }
}
