package fr.sdv.etloff.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.sdv.etloff.domain.Ingredient;

public interface IngredientDao extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByNomIgnoreCase(String nom);

    @Query(value = """
            SELECT i.nom, COUNT(pi.produit_id)
            FROM produit_ingredient pi
            JOIN ingredient i ON i.id = pi.ingredient_id
            GROUP BY i.nom
            ORDER BY COUNT(pi.produit_id) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopByUsage(@Param("limit") int limit);
}
