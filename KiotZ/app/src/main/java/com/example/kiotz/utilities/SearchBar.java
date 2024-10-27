package com.example.kiotz.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SearchBar {
    public static <TIn, TOut> List<TOut> map(List<TIn> source, Function<TIn, TOut> selector) {
        List<TOut> result = new ArrayList<>();
        for (TIn item : source) {
            result.add(selector.apply(item));
        }
        return result;
    }
}