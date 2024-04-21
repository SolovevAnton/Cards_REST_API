package com.solovev.service.imp;

import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.repository.CardRepository;
import com.solovev.service.CardService;
import com.solovev.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CardServiceImp implements CardService {
    private final CategoryService categoryService;
    private final CardRepository repository;

    @Override
    public Collection<Card> getCardsByCategoryId(long catogoryId) {
        return repository.findAllByCategoryId(catogoryId);
    }

    @Override
    public Card getById(long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card with this ID does not exist"));
    }

    @Override
    public Card addCard(long categoryId, Card card) {
        Category foundCategory = categoryService.get(categoryId);
        card.setCategory(foundCategory);
        try {
            return repository.saveAndFlush(card);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(makeErrorMessage(card));
        }
    }

    @Override
    public Card update(Card category) {
        Card old = getById(category.getId());
        try {
            old.setAnswer(category.getAnswer());
            old.setQuestion(category.getQuestion());
            return repository.saveAndFlush(old);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(makeErrorMessage(old));
        }
    }

    @Override
    public Card deleteById(long cardId) {
        Card toDelete = getById(cardId);
        repository.deleteById(cardId);
        return toDelete;
    }

    private String makeErrorMessage(Card card) {
        return String.format("Card with question \"%s\" and answer \"%s\" already exists for category %s",
                card.getQuestion(), card.getAnswer(),card.getCategory().getName());
    }

    @Override
    public Collection<Card> getCardsByUserId(long userId) {
        return repository.findAllByCategory_UserId(userId);
    }
}
