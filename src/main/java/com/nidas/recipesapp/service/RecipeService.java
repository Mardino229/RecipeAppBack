package com.nidas.recipesapp.service;

import com.nidas.recipesapp.Exception.EmailAlreadyExistsException;
import com.nidas.recipesapp.dto.RecipeDtos;
import com.nidas.recipesapp.model.Chief;
import com.nidas.recipesapp.model.Recipe;
import com.nidas.recipesapp.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecipeService {

    private final ChiefRepository chiefRepository;
    private final LikeRepository likeRepository;
    private final FavouritesRepository favouritesRepository;
    private RecipeRepository recipeRepository;
    private ModelMapper modelMapper;
    private ImageService imageService;

    public List<RecipeDtos> getAllRecipes() {
        return recipeRepository.findAll().stream().map(post -> modelMapper.map(post, RecipeDtos.class))
                .collect(Collectors.toList());
    }

    public RecipeDtos getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(()-> new EmailAlreadyExistsException("Recipe not found"));
        return modelMapper.map(recipe, RecipeDtos.class);
    }

    public void saveRecipe(Recipe recipe) {

        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recipe.setChief(chief);
        Chief c = chiefRepository.findByEmail(chief.getEmail()).orElseThrow();
        Recipe save = recipeRepository.save(recipe);
        c.getRecipes().add(save);
        chiefRepository.save(chief);
    }

    public void updateRecipe(Long id, Recipe recipe, MultipartFile file) {

        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chief c = chiefRepository.findByEmail(chief.getEmail()).orElseThrow(()->new EmailAlreadyExistsException("Chief not found"));
        Recipe recipeOptional = recipeRepository.findById(id).orElseThrow(()->new EmailAlreadyExistsException("Recipe not found"));
        if (recipeOptional.getChief()==c) {
            if (file==null || file.isEmpty()) {
                recipeOptional.update(recipe);
            } else{
                String imagePath = recipeOptional.getImageUrl();
                imageService.deleteImage(imagePath);
                recipe.setImageUrl(imageService.saveImage(file));
                recipeOptional.update(recipe);
            }

            recipeRepository.save(recipeOptional);
        } else{
            throw new EmailAlreadyExistsException("Operation Not Authorized");
        }

    }

    @Transactional
    public void deleteRecipe(Long id) {

        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chief c = chiefRepository.findByEmail(chief.getEmail()).orElseThrow(()->new EmailAlreadyExistsException("Chief not found"));
        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new EmailAlreadyExistsException("Recipe not found")
        );
        if (recipe.getChief()==c) {
            String imagePath = recipe.getImageUrl();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
            likeRepository.deleteByRecipe_Id(id);
            favouritesRepository.deleteByRecipe_Id(id);
            recipeRepository.deleteById(id);
        } else{
            throw new EmailAlreadyExistsException("Operation Not Authorized");
        }
    }

}
