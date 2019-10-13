package com.amedigital.wallet.util;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ValidatorUtil {

    private static final String IS_BLANK = "O campo %s é obrigatório.";

    private static String paramMustNotBeNull(String param) {
        return String.format(IS_BLANK, param);
    }

    public static void notEmpty(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new AmeInvalidInputException("wallet_validation", paramMustNotBeNull(field));
        }
    }

    public static <E> void notEmpty(Collection<E> collection, Predicate<E> elementValidation, String field) {
        var isEmptyCollection = Optional.ofNullable(collection)
                .map(Collection::isEmpty)
                .orElse(true);

        if (isEmptyCollection) {
            throw new AmeInvalidInputException("wallet_validation", String.format("Coleção %s deve ter pelo menos um elemento", field));
        }

        if (!collection.stream().allMatch(elementValidation)) {
            throw new AmeInvalidInputException("wallet_validation", String.format("Coleção %s contém elementos inválidos", field));
        }
    }

    public static <T> void notNull(T reference, String field) {
        if (reference == null) {
            throw new AmeInvalidInputException("wallet_validation", paramMustNotBeNull(field));
        }
    }

    public static <K, E> void unused(Map<K, E> reference, String field) {
        if (reference != null && !reference.isEmpty()) {
            throw new AmeInvalidInputException("wallet_validation", String.format("O campo %s não deve ser utilizado nesta ordem", field));
        }
    }

    public static <E> void unused(Collection<E> reference, String field) {
        if (reference != null && !reference.isEmpty()) {
            throw new AmeInvalidInputException("wallet_validation", String.format("O campo %s não deve ser utilizado nesta ordem", field));
        }
    }

    public static <T> void unused(T reference, String field) {
        if (reference != null) {
            throw new AmeInvalidInputException("wallet_validation", String.format("O campo %s não deve ser utilizado nesta ordem", field));
        }
    }
}
