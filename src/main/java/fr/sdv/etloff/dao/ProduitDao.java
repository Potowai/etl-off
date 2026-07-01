package fr.sdv.etloff.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.sdv.etloff.domain.Produit;

public interface ProduitDao extends JpaRepository<Produit, Long> {

    @Query(value = """
            SELECT p.* FROM produit p
            JOIN marque m ON m.id = p.marque_id
            WHERE LOWER(m.nom) = LOWER(:brand)
            ORDER BY CASE p.nutrition_grade_fr
                WHEN 'A' THEN 1 WHEN 'B' THEN 2 WHEN 'C' THEN 3
                WHEN 'D' THEN 4 WHEN 'E' THEN 5 WHEN 'F' THEN 6 ELSE 7 END,
                COALESCE(p.energie100g, 999999)
            LIMIT :limit
            """, nativeQuery = true)
    List<Produit> findTopByBrand(@Param("brand") String brand, @Param("limit") int limit);

    @Query(value = """
            SELECT p.* FROM produit p
            JOIN categorie c ON c.id = p.categorie_id
            WHERE LOWER(c.nom) = LOWER(:category)
            ORDER BY CASE p.nutrition_grade_fr
                WHEN 'A' THEN 1 WHEN 'B' THEN 2 WHEN 'C' THEN 3
                WHEN 'D' THEN 4 WHEN 'E' THEN 5 WHEN 'F' THEN 6 ELSE 7 END,
                COALESCE(p.energie100g, 999999)
            LIMIT :limit
            """, nativeQuery = true)
    List<Produit> findTopByCategory(@Param("category") String category, @Param("limit") int limit);

    @Query(value = """
            SELECT p.* FROM produit p
            JOIN marque m ON m.id = p.marque_id
            JOIN categorie c ON c.id = p.categorie_id
            WHERE LOWER(m.nom) = LOWER(:brand) AND LOWER(c.nom) = LOWER(:category)
            ORDER BY CASE p.nutrition_grade_fr
                WHEN 'A' THEN 1 WHEN 'B' THEN 2 WHEN 'C' THEN 3
                WHEN 'D' THEN 4 WHEN 'E' THEN 5 WHEN 'F' THEN 6 ELSE 7 END,
                COALESCE(p.energie100g, 999999)
            LIMIT :limit
            """, nativeQuery = true)
    List<Produit> findTopByBrandAndCategory(
            @Param("brand") String brand,
            @Param("category") String category,
            @Param("limit") int limit);

    @Query("SELECT COUNT(p) FROM Produit p WHERE LOWER(p.marque.nom) = LOWER(:nom)")
    long countByBrandName(@Param("nom") String nom);

    @Query("SELECT COUNT(p) FROM Produit p WHERE LOWER(p.categorie.nom) = LOWER(:nom)")
    long countByCategoryName(@Param("nom") String nom);
}
