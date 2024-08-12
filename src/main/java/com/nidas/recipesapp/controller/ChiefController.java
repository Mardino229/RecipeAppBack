package com.nidas.recipesapp.controller;

import com.nidas.recipesapp.config.JwtService;
import com.nidas.recipesapp.dto.*;
import com.nidas.recipesapp.model.Chief;
import com.nidas.recipesapp.service.ChiefService;
import com.nidas.recipesapp.service.FavouritesService;
import com.nidas.recipesapp.service.LikesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class ChiefController {

    private final ChiefService chiefService;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private LikesService likesService;
    private FavouritesService favouritesService;

    @PostMapping(path = "register")
    public void signup(@RequestBody Chief chief) {
        chiefService.inscription(chief);
    }

    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String, String> activation) {
        log.info("Activation");
        this.chiefService.activation(activation);
    }

    @PostMapping(path = "login")
    public ResponseEntity<?> login(@RequestBody AuthentificationDto authentificationDto) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDto.username(), authentificationDto.password())
        );
        log.info("resultat {} ", authenticate.isAuthenticated());
        Map<String, String> generate = jwtService.generate(authentificationDto.username());
        Map<String, String> body = java.util.Map.of("accessToken", generate.get("accessToken"), "roles", generate.get("roles"));
        if (authenticate.isAuthenticated()) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", generate.get("refreshToken"))
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 jours
                    .build();
            return  ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(body);
        }
        return null;
    }


    @PostMapping(path = "refresh-token")
    public ResponseEntity<?>  refreshToken(@CookieValue("refreshToken") String refreshTokenRequest) {
        Map<String, String> Map = jwtService.refreshToken(refreshTokenRequest);
        Map<String, String> body = java.util.Map.of("accessToken", Map.get("accessToken"), "roles", Map.get("roles"));
        ResponseCookie cookie = ResponseCookie.from("refreshToken", Map.get("refreshToken"))
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 jours
                .build();
        return  ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }

    @PostMapping("modifier-password")
    public void resetPassword(@RequestBody Map<String, String> activation){
        chiefService.setPassword(activation);
    }

    @PostMapping("nouveau-password")
    public void newPassword(@RequestBody Map<String, String> activation){
        chiefService.newPassword(activation);
    }


    @PostMapping(path = "deconnexion")
    public void logout(@CookieValue("refreshToken") String refreshTokenRequest) {
        jwtService.logout(refreshTokenRequest);
    }

    //@PreAuthorize("hasAuthority('TYPE_ADMIN')")
    @GetMapping(path = "chief")
    public List<ChiefDto> printAllChief(){
        return chiefService.printAllChief();
    }

    @GetMapping(path = "/chiefrecipe")
    public List<RecipeDto> getChiefRecipe(){
        return chiefService.getChiefRecipe();
    }

    @PostMapping(path = "/like/{id}")
    public void likeRecipe(@PathVariable Long id){
        likesService.likeRecipe(id);
    }

    @GetMapping("/hasLiked/{id}")
    public ResponseEntity<Boolean> hasLiked(@PathVariable Long id) {
        boolean hasLiked = likesService.hasLikedRecipe(id);
        return ResponseEntity.ok(hasLiked);
    }

    @PostMapping(path = "/favourite/{id}")
    public void favorite(@PathVariable Long id){
        favouritesService.addFavorite(id);
    }

    @GetMapping("/hasFavourite/{id}")
    public ResponseEntity<Boolean> hasFavorite(@PathVariable Long id) {
        boolean hasFavourite = favouritesService.hasFavouritesRecipe(id);
        return ResponseEntity.ok(hasFavourite);
    }

    @GetMapping(path = "/favourites")
    public List<RecipeDtos> getMyFavourites() {
        return chiefService.getMyFavourites();
    }

    @GetMapping(path = "verify-recipe/{id}")
    public boolean verify(@PathVariable Long id)  {
        return chiefService.verify(id);
    }

    @GetMapping("/hasCreated/{id}")
    public ResponseEntity<Boolean> hasCreated(@PathVariable Long id) {
        boolean hasCreated = chiefService.hasCreatedRecipe(id);
        return ResponseEntity.ok(hasCreated);
    }
}