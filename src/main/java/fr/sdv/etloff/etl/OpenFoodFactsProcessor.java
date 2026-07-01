package fr.sdv.etloff.etl;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import fr.sdv.etloff.domain.Produit;
import fr.sdv.etloff.parser.CsvLineParser;
import fr.sdv.etloff.service.IReferenceDataService;

@Component
public class OpenFoodFactsProcessor implements ItemProcessor<String, Produit> {

    private final IReferenceDataService referenceDataService;

    public OpenFoodFactsProcessor(IReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @Override
    public Produit process(String line) {
        CsvProductRecord record = CsvLineParser.parseLine(line);
        if (record == null || record.nom() == null || record.nom().isBlank()) return null;
        Produit p = new Produit();
        p.setNom(record.nom());
        p.setNutritionGradeFr(record.nutritionGradeFr());
        p.setIngredientsText(record.ingredientsRaw());
        p.setEnergie100g(record.energie100g());
        p.setGraisse100g(record.graisse100g());
        p.setSucres100g(record.sucres100g());
        p.setFibres100g(record.fibres100g());
        p.setProteines100g(record.proteines100g());
        p.setSel100g(record.sel100g());
        p.setVitA100g(record.vitA100g());
        p.setVitD100g(record.vitD100g());
        p.setVitE100g(record.vitE100g());
        p.setVitK100g(record.vitK100g());
        p.setVitC100g(record.vitC100g());
        p.setVitB1100g(record.vitB1100g());
        p.setVitB2100g(record.vitB2100g());
        p.setVitPP100g(record.vitPP100g());
        p.setVitB6100g(record.vitB6100g());
        p.setVitB9100g(record.vitB9100g());
        p.setVitB12100g(record.vitB12100g());
        p.setCalcium100g(record.calcium100g());
        p.setMagnesium100g(record.magnesium100g());
        p.setIron100g(record.iron100g());
        p.setFer100g(record.fer100g());
        p.setBetaCarotene100g(record.betaCarotene100g());
        p.setPresenceHuilePalme(record.presenceHuilePalme());
        if (record.categorie() != null && !record.categorie().isBlank())
            p.setCategorie(referenceDataService.findCategorie(record.categorie()));
        if (record.marque() != null && !record.marque().isBlank())
            p.setMarque(referenceDataService.findMarque(record.marque()));
        linkAll(p.getIngredients(), record.ingredients(), referenceDataService::findIngredient);
        linkAll(p.getAllergenes(), record.allergenes(), referenceDataService::findAllergene);
        linkAll(p.getAdditifs(), record.additifs(), referenceDataService::findAdditif);
        return p;
    }

    private <T> void linkAll(Set<T> target, List<String> names, Function<String, T> finder) {
        if (names == null) return;
        for (String name : names) {
            T entity = finder.apply(name);
            if (entity != null) target.add(entity);
        }
    }
}
