package com.solovev.controller;

import com.solovev.dto.ResponseResult;
import com.solovev.model.Card;
import com.solovev.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<ResponseResult<Collection<Card>>> findByCategoryId(@RequestParam long categoryId){
        return ResponseEntity.ok(new ResponseResult<>(null,cardService.getCardsByCategoryId(categoryId)));
    }



}
