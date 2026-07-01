package fr.sdv.etloff.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.sdv.etloff.domain.Additif;

public interface AdditifDao extends JpaRepository<Additif, Long> {

    Optional<Additif> findByNomIgnoreCase(String nom);

    @Query(value = """
            SELECT a.nom, COUNT(pa.produit_id)
            FROM produit_additif pa
            JOIN additif a ON a.id = pa.additif_id
            GROUP BY a.nom
            ORDER BY COUNT(pa.produit_id) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopByUsage(@Param("limit") int limit);
}
