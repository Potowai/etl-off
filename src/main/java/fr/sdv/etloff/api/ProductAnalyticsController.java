package fr.sdv.etloff.api;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.sdv.etloff.api.dto.ElementCountDto;
import fr.sdv.etloff.api.dto.ProduitDto;
import fr.sdv.etloff.service.ProductAnalyticsService;
import jakarta.validation.constraints.Min;

@RestController
@Validated
public class ProductAnalyticsController {

    private final ProductAnalyticsService analyticsService;

    public ProductAnalyticsController(ProductAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/products/top-by-brand")
    public List<ProduitDto> topByBrand(
            @RequestParam String brand,
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        return analyticsService.topByBrand(brand, limit);
    }

    @GetMapping("/products/top-by-category")
    public List<ProduitDto> topByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        return analyticsService.topByCategory(category, limit);
    }

    @GetMapping("/products/top-by-brand-category")
    public List<ProduitDto> topByBrandAndCategory(
            @RequestParam String brand,
            @RequestParam String category,
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        return analyticsService.topByBrandAndCategory(brand, category, limit);
    }

    @GetMapping("/ingredients/top")
    public List<ElementCountDto> topIngredients(@RequestParam(defaultValue = "10") @Min(1) int limit) {
        return analyticsService.topIngredients(limit);
    }

    @GetMapping("/allergens/top")
    public List<ElementCountDto> topAllergens(@RequestParam(defaultValue = "10") @Min(1) int limit) {
        return analyticsService.topAllergens(limit);
    }

    @GetMapping("/additives/top")
    public List<ElementCountDto> topAdditives(@RequestParam(defaultValue = "10") @Min(1) int limit) {
        return analyticsService.topAdditives(limit);
    }
}
