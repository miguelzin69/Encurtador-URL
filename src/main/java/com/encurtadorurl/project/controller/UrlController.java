package com.encurtadorurl.project.controller;

import java.net.URI;
import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.encurtadorurl.project.dto.shortenedDto;
import com.encurtadorurl.project.dto.urlDto;
import com.encurtadorurl.project.entities.UrlEntity;
import com.encurtadorurl.project.repository.UrlRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@ResponseBody
public class UrlController {

    private UrlRepository urlRepository;

    public UrlController(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }
    
    @PostMapping("/shorten-url")
    public ResponseEntity<shortenedDto> shortenUrl(@RequestBody urlDto urlDto, HttpServletRequest httpServletRequest) {
        String id;

        // Generate unique id
        do {
            id = RandomStringUtils.randomAlphanumeric(5, 10);
        } while (urlRepository.existsById(id));

        // Save the URL entity with the full URL and expiration time
        UrlEntity urlEntity = new UrlEntity(id, urlDto.urlString(), LocalDateTime.now().plusMinutes(1));
        urlRepository.save(urlEntity);

        // Generate shortened URL
        var shortenedUrl = httpServletRequest.getRequestURL().toString().replace("shorten-url", id);

        // Return shortened URL in the response
        return ResponseEntity.ok(new shortenedDto(shortenedUrl));
    }

    @GetMapping("{id}")
    public ResponseEntity<Void> redirect(@PathVariable("id") String id) {

        var url = urlRepository.findById(id);

        if (url.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url.get().getFullUrl()));

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }
}
