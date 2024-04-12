package com.solovev.controller;

import com.solovev.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

   /* @GetMapping
    public ResponseEntity<ResponseResult<Collection<Card>>> findByCategoryId(@RequestParam long categoryId){
        return ResponseEntity.ok(ResponseResult.of(cardService.getCardsByCategoryId(categoryId)));*/

}
