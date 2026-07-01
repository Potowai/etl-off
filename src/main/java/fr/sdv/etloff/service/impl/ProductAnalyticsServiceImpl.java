package fr.sdv.etloff.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import fr.sdv.etloff.api.dto.ElementCountDto;
import fr.sdv.etloff.api.dto.ProduitDto;
import fr.sdv.etloff.dao.AdditifDao;
import fr.sdv.etloff.dao.AllergeneDao;
import fr.sdv.etloff.dao.IngredientDao;
import fr.sdv.etloff.dao.ProduitDao;
import fr.sdv.etloff.service.IProductAnalyticsService;

@Service
@Transactional(readOnly = true)
public class ProductAnalyticsServiceImpl implements IProductAnalyticsService {

    private final ProduitDao produitDao;
    private final IngredientDao ingredientDao;
    private final AllergeneDao allergeneDao;
    private final AdditifDao additifDao;

    public ProductAnalyticsServiceImpl(
            ProduitDao produitDao,
            IngredientDao ingredientDao,
            AllergeneDao allergeneDao,
            AdditifDao additifDao) {
        this.produitDao = produitDao;
        this.ingredientDao = ingredientDao;
        this.allergeneDao = allergeneDao;
        this.additifDao = additifDao;
    }

    @Override
    public List<ProduitDto> topByBrand(String brand, int limit) {
        requireBrand(brand);
        return produitDao.findTopByBrand(brand, limit).stream().map(ProduitDto::from).toList();
    }

    @Override
    public List<ProduitDto> topByCategory(String category, int limit) {
        requireCategory(category);
        return produitDao.findTopByCategory(category, limit).stream().map(ProduitDto::from).toList();
    }

    @Override
    public List<ProduitDto> topByBrandAndCategory(String brand, String category, int limit) {
        requireBrand(brand);
        requireCategory(category);
        return produitDao.findTopByBrandAndCategory(brand, category, limit).stream().map(ProduitDto::from).toList();
    }

    @Override
    public List<ElementCountDto> topIngredients(int limit) {
        return mapCounts(ingredientDao.findTopByUsage(limit));
    }

    @Override
    public List<ElementCountDto> topAllergens(int limit) {
        return mapCounts(allergeneDao.findTopByUsage(limit));
    }

    @Override
    public List<ElementCountDto> topAdditives(int limit) {
        return mapCounts(additifDao.findTopByUsage(limit));
    }

    private void requireBrand(String brand) {
        if (produitDao.countByBrandName(brand) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marque introuvable : " + brand);
    }

    private void requireCategory(String category) {
        if (produitDao.countByCategoryName(category) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable : " + category);
    }

    private List<ElementCountDto> mapCounts(List<Object[]> rows) {
        return rows.stream().map(r -> new ElementCountDto((String) r[0], ((Number) r[1]).longValue())).toList();
    }
}
