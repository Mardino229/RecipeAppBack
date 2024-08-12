package com.nidas.recipesapp.service;

import com.nidas.recipesapp.Exception.EmailAlreadyExistsException;
import com.nidas.recipesapp.model.Chief;
import com.nidas.recipesapp.model.Favourites;
import com.nidas.recipesapp.model.Recipe;
import com.nidas.recipesapp.repository.ChiefRepository;
import com.nidas.recipesapp.repository.FavouritesRepository;
import com.nidas.recipesapp.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FavouritesService {

    private static final Logger log = LoggerFactory.getLogger(FavouritesService.class);
    private FavouritesRepository favoriteRepository;
    private RecipeRepository recipeRepository;
    private ChiefRepository chiefRepository;

    public boolean hasFavouritesRecipe(Long recipeId) {
        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return favoriteRepository.countByChiefIdAndRecipeId(chief.getId(), recipeId) > 0;
    }

    public void addFavorite(Long recipeId) {
        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe byId = recipeRepository.findById(recipeId).orElseThrow(()->new EmailAlreadyExistsException("Recipe not found"));
        if (!hasFavouritesRecipe(recipeId)) {
            Favourites favourite = new Favourites();
            favourite.setChief(chief);
            favourite.setRecipe(byId);
            byId.setNbFavourite(byId.getNbFavourite()+1);
            recipeRepository.save(byId);
            favoriteRepository.save(favourite);
        } else {
            Favourites favourite = favoriteRepository.findByChiefIdAndRecipeId(chief.getId(), recipeId);
            if (favourite != null) {
                favoriteRepository.delete(favourite);
                byId.setNbFavourite(byId.getNbFavourite()-1);
                recipeRepository.save(byId);
            }
        }

    }
}
