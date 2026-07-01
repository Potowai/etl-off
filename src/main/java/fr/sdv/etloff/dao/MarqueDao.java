package fr.sdv.etloff.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.sdv.etloff.domain.Marque;

public interface MarqueDao extends JpaRepository<Marque, Long> {

    Optional<Marque> findByNomIgnoreCase(String nom);
}
