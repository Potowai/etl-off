package fr.sdv.etloff.service;

import java.util.List;

import fr.sdv.etloff.api.dto.ElementCountDto;
import fr.sdv.etloff.api.dto.ProduitDto;

public interface IProductAnalyticsService {
    List<ProduitDto> topByBrand(String brand, int limit);
    List<ProduitDto> topByCategory(String category, int limit);
    List<ProduitDto> topByBrandAndCategory(String brand, String category, int limit);
    List<ElementCountDto> topIngredients(int limit);
    List<ElementCountDto> topAllergens(int limit);
    List<ElementCountDto> topAdditives(int limit);
}
