package com.duoc.semana2.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duoc.semana2.model.Receta;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    // --- FILTROS ---
    @Query("""
        SELECT r FROM Receta r WHERE
        (:nombre IS NULL OR LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND (:tipoCocina IS NULL OR :tipoCocina = '' OR r.tipoCocina = :tipoCocina)
        AND (:pais IS NULL OR :pais = '' OR r.pais = :pais)
        AND (:dificultad IS NULL OR :dificultad = '' OR r.dificultad = :dificultad)
        AND (:ingredientes IS NULL OR LOWER(r.ingredientes) LIKE LOWER(CONCAT('%', :ingredientes, '%')))
    """)
    List<Receta> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("tipoCocina") String tipoCocina,
            @Param("ingredientes") String ingredientes,
            @Param("pais") String pais,
            @Param("dificultad") String dificultad
    );

    // --- FALTABA ESTE (LO USA EL TEST) ---
    List<Receta> findByNombreContainingIgnoreCase(String nombre);

    // --- POPULARES ---
    @Query("SELECT r FROM Receta r ORDER BY r.vistas DESC, r.likes DESC")
    List<Receta> findTopRecetas(Pageable pageable);

    // --- RECIENTES ---
    @Query("SELECT r FROM Receta r ORDER BY r.fechaCreacion DESC")
    List<Receta> findRecentRecetas(Pageable pageable);

    // --- POR USUARIO ---
    List<Receta> findByUsuarioId(Long usuarioId);

    // --- FAVORITAS ---
    @Query("""
        SELECT r FROM Receta r
        JOIN r.usuariosQueFavoritearon u
        WHERE u.id = :usuarioId
    """)
    List<Receta> findFavoritasByUsuarioId(@Param("usuarioId") Long usuarioId);
}
