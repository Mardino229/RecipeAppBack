package com.nidas.recipesapp.service;

import com.nidas.recipesapp.Exception.EmailAlreadyExistsException;
import com.nidas.recipesapp.dto.ChiefDto;
import com.nidas.recipesapp.dto.RecipeDto;
import com.nidas.recipesapp.dto.RecipeDtos;
import com.nidas.recipesapp.model.*;
import com.nidas.recipesapp.repository.ChiefRepository;
import com.nidas.recipesapp.repository.FavouritesRepository;
import com.nidas.recipesapp.repository.RecipeRepository;
import com.nidas.recipesapp.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ChiefService implements UserDetailsService {
    private final RecipeRepository recipeRepository;
    private final FavouritesRepository favouritesRepository;
    private ModelMapper modelMapper;
    private RoleRepository repository;
    private ChiefRepository chiefRepository;
    private ValidationService validationService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void inscription(Chief chief) {

        if (chiefRepository.existsByEmail(chief.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (chiefRepository.existsByPseudo(chief.getPseudo())) {
            throw new EmailAlreadyExistsException("Pseudo already exists");
        }

        chief.setPassword(bCryptPasswordEncoder.encode(chief.getPassword()));
        Role role = repository.findByType(TypeRole.USER);
        chief.setRole(role);
        chiefRepository.save(chief);
        validationService.enregistrer(chief);
    }

    @Override
    public Chief loadUserByUsername(String username) throws UsernameNotFoundException {
        return chiefRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("No user correspond to email"));
    }

    public void setPassword(Map<String, String> activation) {

        Chief chief = chiefRepository.findByEmail(activation.get("username")).orElseThrow(() -> new EmailAlreadyExistsException("No user correspond to email"));

        validationService.enregistrer(chief);
    }

    public void activation(Map<String, String> activation) {
        Validation validation = validationService.verif(activation.get("code"));
        if(Instant.now().isAfter(validation.getExpiration())){
            throw new EmailAlreadyExistsException("Votre code a expiré");
        }
        Chief user = chiefRepository.findById(validation.getChief().getId()).orElseThrow(
                ()-> new EmailAlreadyExistsException("No user correspond to email")
        );
        user.setEnabled(true);
        chiefRepository.save(user);
    }

    public void newPassword(Map<String, String> activation) {

        Chief chief = chiefRepository.findByEmail(activation.get("username")).orElseThrow(() -> new EmailAlreadyExistsException("No user correspond to email"));
        final Validation code = validationService.verif(activation.get("code"));
        if (code.getChief().getEmail().equals(chief.getEmail())) {
            String mdp = bCryptPasswordEncoder.encode(activation.get("password"));
            chief.setPassword(mdp);
            chiefRepository.save(chief);
            validationService.delete(activation.get("username"));
        }else {
            throw new EmailAlreadyExistsException("Code incorrect");
        }

    }

    public List<ChiefDto> printAllChief() {
        return chiefRepository.findAll().stream().map(post -> modelMapper.map(post, ChiefDto.class))
               .collect(Collectors.toList());
    }

    public List<RecipeDto> getChiefRecipe() {
        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return chiefRepository.findChiefById(chief.getId()).getRecipes().stream().map(post -> modelMapper.map(post, RecipeDto.class))
                .collect(Collectors.toList());
    }

    public boolean verify(Long id) {

        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chief c = chiefRepository.findByEmail(chief.getEmail()).orElseThrow(()->new EmailAlreadyExistsException("Chief not found"));
        Recipe recipe = recipeRepository.findById(id).orElseThrow(()-> new EmailAlreadyExistsException("Recipe not found"));
        return c.getRecipes().contains(recipe);
    }



    public List<RecipeDtos> getMyFavourites() {

        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chief c = chiefRepository.findByEmail(chief.getEmail()).orElseThrow(()->new EmailAlreadyExistsException("Chief not found"));
        List<Favourites> allByChief = favouritesRepository.findAllByChief(chief);
        List<Recipe> myFavour = new ArrayList<>();
        for(Favourites favour : allByChief ){
            myFavour.add(favour.getRecipe());
        }
        return myFavour.stream().map(post -> modelMapper.map(post, RecipeDtos.class))
                .collect(Collectors.toList());
    }

    public boolean hasCreatedRecipe(Long id) {
        Chief chief = (Chief) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe byId = recipeRepository.findById(id).orElseThrow(()->new EmailAlreadyExistsException("Recipe not found"));

        return Objects.equals(byId.getChief().getId(), chief.getId());
    }
}
