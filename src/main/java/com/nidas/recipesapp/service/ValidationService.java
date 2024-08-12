package com.nidas.recipesapp.service;

import com.nidas.recipesapp.Exception.EmailAlreadyExistsException;
import com.nidas.recipesapp.model.Chief;
import com.nidas.recipesapp.model.Validation;
import com.nidas.recipesapp.repository.ValidationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@AllArgsConstructor
@Transactional
public class ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);
    private ValidationRepository validationRepository;
    private NotificationService notificationService;

    public void enregistrer(Chief user) {
        Validation validation = new Validation();
        validation.setChief(user);
        Instant creation = Instant.now();
        validation.setCreation(creation);
        Instant expiration = creation.plus(10, ChronoUnit.MINUTES);
        validation.setExpiration(expiration);
        Random random = new Random();
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d", randomInteger);

        validation.setCode(code);
        try{
            validationRepository.save(validation);
            notificationService.envoyer(validation);
        }catch (DataIntegrityViolationException e){
            throw new EmailAlreadyExistsException("Vous avez déja demandé un code de validation. Réessayez dans 10minutes");
        }


    }

    public Validation verif(String code){

       return validationRepository.findByCode(code).orElseThrow(
                ()->new EmailAlreadyExistsException("Votre code est invalide")
        );
    }

    public void delete(String email){
        validationRepository.deleteByChief_Email(email);
    }

    @Scheduled(cron = "0 * * * * *")
    public void nettoyage(){
        log.info("Supression des codes de validation: {}", Instant.now());
        validationRepository.deleteAllByExpirationBefore(Instant.now());
    }

}
