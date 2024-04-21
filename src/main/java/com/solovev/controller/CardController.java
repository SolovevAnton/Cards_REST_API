package com.solovev.controller;

import com.solovev.dto.ResponseResult;
import com.solovev.model.Card;
import com.solovev.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/{categoryId}")
    public ResponseEntity<ResponseResult<Card>> addCard(@PathVariable long categoryId, @RequestBody Card card){
        return ResponseEntity.ok(new ResponseResult<>(null,cardService.addCard(categoryId,card)));
    }

    @PutMapping
    public ResponseEntity<ResponseResult<Card>> updateCard(@RequestBody Card card){
        return ResponseEntity.ok(new ResponseResult<>(null,cardService.update(card)));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<ResponseResult<Card>> deleteCard(@PathVariable long cardId){
        Card toDelete = cardService.deleteById(cardId);
        return  ResponseEntity.ok(new ResponseResult<>(null,toDelete));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseResult<Card>> dbExceptionHandler(IllegalArgumentException e){
        return new ResponseEntity<>(new ResponseResult<>(e.getMessage(),null), HttpStatus.CONFLICT);
    }

}
