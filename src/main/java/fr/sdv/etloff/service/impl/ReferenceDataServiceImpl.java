package fr.sdv.etloff.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.sdv.etloff.dao.AdditifDao;
import fr.sdv.etloff.dao.AllergeneDao;
import fr.sdv.etloff.dao.CategorieDao;
import fr.sdv.etloff.dao.IngredientDao;
import fr.sdv.etloff.dao.MarqueDao;
import fr.sdv.etloff.domain.Additif;
import fr.sdv.etloff.domain.Allergene;
import fr.sdv.etloff.domain.Categorie;
import fr.sdv.etloff.domain.Ingredient;
import fr.sdv.etloff.domain.Marque;
import fr.sdv.etloff.service.IReferenceDataService;

@Service
public class ReferenceDataServiceImpl implements IReferenceDataService {

    private final CategorieDao categorieDao;
    private final MarqueDao marqueDao;
    private final IngredientDao ingredientDao;
    private final AllergeneDao allergeneDao;
    private final AdditifDao additifDao;

    private final ConcurrentHashMap<String, Categorie> categories = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Marque> marques = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Ingredient> ingredients = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Allergene> allergenes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Additif> additifs = new ConcurrentHashMap<>();

    public ReferenceDataServiceImpl(
            CategorieDao categorieDao,
            MarqueDao marqueDao,
            IngredientDao ingredientDao,
            AllergeneDao allergeneDao,
            AdditifDao additifDao) {
        this.categorieDao = categorieDao;
        this.marqueDao = marqueDao;
        this.ingredientDao = ingredientDao;
        this.allergeneDao = allergeneDao;
        this.additifDao = additifDao;
    }

    @Override
    @Transactional
    public void bulkLoad(Set<String> categories, Set<String> marques, Set<String> ingredients,
                         Set<String> allergenes, Set<String> additifs) {
        this.categories.clear();
        this.marques.clear();
        this.ingredients.clear();
        this.allergenes.clear();
        this.additifs.clear();
        saveAllCategories(categories);
        saveAllMarques(marques);
        saveAllIngredients(ingredients);
        saveAllAllergenes(allergenes);
        saveAllAdditifs(additifs);
    }

    @Override
    public Categorie findCategorie(String nom) { return lookup(nom, this.categories); }

    @Override
    public Marque findMarque(String nom) { return lookup(nom, this.marques); }

    @Override
    public Ingredient findIngredient(String nom) { return lookup(nom, this.ingredients); }

    @Override
    public Allergene findAllergene(String nom) { return lookup(nom, this.allergenes); }

    @Override
    public Additif findAdditif(String nom) { return lookup(nom, this.additifs); }

    private void saveAllCategories(Set<String> names) {
        List<Categorie> entities = new ArrayList<>(names.size());
        for (String nom : names) entities.add(new Categorie(nom));
        categorieDao.saveAll(entities).forEach(c -> this.categories.put(c.getNom().toLowerCase(), c));
    }

    private void saveAllMarques(Set<String> names) {
        List<Marque> entities = new ArrayList<>(names.size());
        for (String nom : names) entities.add(new Marque(nom));
        marqueDao.saveAll(entities).forEach(m -> this.marques.put(m.getNom().toLowerCase(), m));
    }

    private void saveAllIngredients(Set<String> names) {
        List<Ingredient> entities = new ArrayList<>(names.size());
        for (String nom : names) entities.add(new Ingredient(nom));
        ingredientDao.saveAll(entities).forEach(i -> this.ingredients.put(i.getNom().toLowerCase(), i));
    }

    private void saveAllAllergenes(Set<String> names) {
        List<Allergene> entities = new ArrayList<>(names.size());
        for (String nom : names) entities.add(new Allergene(nom));
        allergeneDao.saveAll(entities).forEach(a -> this.allergenes.put(a.getNom().toLowerCase(), a));
    }

    private void saveAllAdditifs(Set<String> names) {
        List<Additif> entities = new ArrayList<>(names.size());
        for (String nom : names) entities.add(new Additif(nom));
        additifDao.saveAll(entities).forEach(a -> this.additifs.put(a.getNom().toLowerCase(), a));
    }

    private static <T> T lookup(String nom, ConcurrentHashMap<String, T> cache) {
        if (nom == null || nom.isBlank()) return null;
        return cache.get(nom.toLowerCase());
    }
}
