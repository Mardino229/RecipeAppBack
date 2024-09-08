package com.nidas.recipesapp.repository;

import com.nidas.recipesapp.model.Chief;
import com.nidas.recipesapp.model.Favourites;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouritesRepository extends JpaRepository<Favourites, Long> {

    boolean existsByChiefIdAndRecipeId(Long chiefId, Long recipeId);
    Favourites findByChiefIdAndRecipeId(Long chiefId, Long recipeId);
    long countByChiefIdAndRecipeId(Long chiefId, Long recipeId);
    List<Favourites> findAllByChief(Chief chief);
    void deleteAllByRecipe_Id(Long id);
    void deleteByRecipe_Id(Long id);
}
