package fr.sdv.etloff.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.sdv.etloff.domain.Categorie;

public interface CategorieDao extends JpaRepository<Categorie, Long> {

    Optional<Categorie> findByNomIgnoreCase(String nom);
}
