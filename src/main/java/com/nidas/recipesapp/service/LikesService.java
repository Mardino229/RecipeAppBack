package com.nidas.recipesapp.service;

import com.nidas.recipesapp.Exception.EmailAlreadyExistsException;
import com.nidas.recipesapp.model.Chief;
import com.nidas.recipesapp.model.Likes;
import com.nidas.recipesapp.model.Recipe;
import com.nidas.recipesapp.repository.ChiefRepository;
import com.nidas.recipesapp.repository.LikeRepository;
import com.nidas.recipesapp.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LikesService {

    private static final Logger log = LoggerFactory.getLogger(LikesService.class);
    private LikeRepository likesRepository;
    private RecipeRepository recipeRepository;
    private ChiefRepository chiefRepository;

    public boolean hasLikedRecipe(Long recipeId) {
        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return likesRepository.countByChiefIdAndRecipeId(chief.getId(), recipeId) > 0;
    }

    public void likeRecipe(Long recipeId) {
        Recipe byId = recipeRepository.findById(recipeId).orElseThrow(()->new EmailAlreadyExistsException("Recipe not found"));
        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!hasLikedRecipe(recipeId)) {
            Likes like = new Likes();
            like.setChief(chief);
            like.setRecipe(byId);
            likesRepository.save(like);
            byId.setNbLike(byId.getNbLike()+1);
            recipeRepository.save(byId);
        } else {
            Likes like = likesRepository.findByChiefIdAndRecipeId(chief.getId(), recipeId);
            if (like != null) {
                likesRepository.delete(like);
                byId.setNbLike(byId.getNbLike()-1);
                recipeRepository.save(byId);
            }
        }
    }
}
