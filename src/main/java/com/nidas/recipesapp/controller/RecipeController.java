package com.nidas.recipesapp.controller;

import com.nidas.recipesapp.dto.RecipeDtos;
import com.nidas.recipesapp.model.Recipe;
import com.nidas.recipesapp.service.ImageService;
import com.nidas.recipesapp.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@RestController
@AllArgsConstructor
@RequestMapping(path = "/recipe")
public class RecipeController {

    private ImageService imageService;
    private RecipeService recipeService;

    @GetMapping
    public List<RecipeDtos> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public RecipeDtos getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Recipe> createRecipe( @RequestPart("recipe") Recipe recipe, @RequestPart("image") MultipartFile file) {
        recipe.setImageUrl(imageService.saveImage(file));
        recipeService.saveRecipe(recipe);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @PutMapping("/{id}")
    public void updateRecipe(@PathVariable Long id, @RequestPart("recipe") Recipe recipe, @RequestPart(value = "image", required = false) MultipartFile file) {
         recipeService.updateRecipe(id, recipe,file);
    }


    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
    }
}
