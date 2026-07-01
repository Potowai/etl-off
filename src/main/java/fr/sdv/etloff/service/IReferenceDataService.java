package fr.sdv.etloff.service;

import java.util.Set;

import fr.sdv.etloff.domain.Additif;
import fr.sdv.etloff.domain.Allergene;
import fr.sdv.etloff.domain.Categorie;
import fr.sdv.etloff.domain.Ingredient;
import fr.sdv.etloff.domain.Marque;

public interface IReferenceDataService {
    void bulkLoad(Set<String> categories, Set<String> marques, Set<String> ingredients,
                  Set<String> allergenes, Set<String> additifs);
    Categorie findCategorie(String nom);
    Marque findMarque(String nom);
    Ingredient findIngredient(String nom);
    Allergene findAllergene(String nom);
    Additif findAdditif(String nom);
}
