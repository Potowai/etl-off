package fr.sdv.etloff.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.sdv.etloff.domain.Allergene;

public interface AllergeneDao extends JpaRepository<Allergene, Long> {

    Optional<Allergene> findByNomIgnoreCase(String nom);

    @Query(value = """
            SELECT a.nom, COUNT(pa.produit_id)
            FROM produit_allergene pa
            JOIN allergene a ON a.id = pa.allergene_id
            GROUP BY a.nom
            ORDER BY COUNT(pa.produit_id) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopByUsage(@Param("limit") int limit);
}
