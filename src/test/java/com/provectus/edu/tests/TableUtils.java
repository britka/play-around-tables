package com.provectus.edu.tests;

import com.codeborne.selenide.SelenideElement;
import com.microsoft.playwright.Locator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.Annotation;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableUtils {
    public static <T> List<T> getTable(Locator tableLocator, String rowsLocator, Class<T> rowModel) {
        String outerHTML = tableLocator.evaluate("node => node.outerHTML").toString();
        try {
            return parseToList(outerHTML, rowsLocator, rowModel);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> getTable(SelenideElement tableLocator, String rowsLocator, Class<T> rowModel) {
        String outerHTML = tableLocator.attr("outerHTML");
        try {
            return parseToList(outerHTML, rowsLocator, rowModel);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private static <T> List<T> parseToList(String outerHtml, String rowsLocator, Class<T> rowModel)
            throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        // Create instance of model
        List<T> result = new ArrayList<>();
        // Create Jsoup document
        Document document = Jsoup.parse(outerHtml);
        Elements rows;
        if (rowsLocator.startsWith("xpath")) {
            rows = document.selectXpath(rowsLocator.replace("xpath=", ""));
        } else {
            rows = document.select(rowsLocator);
        }
        for (Element element : rows) {
            Object model = rowModel.getConstructor().newInstance();
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ElementLink.class)) {
                    String value;
                    ElementLink annotation = field.getAnnotation(ElementLink.class);
                    String locator = annotation.locator();
                    if (locator.startsWith("xpath=")) {
                        value = element.selectXpath(locator.replace("xpath=", "")).text();
                    } else {
                        value = element.select(locator).text();
                    }

                    if (field.getType().equals(LocalDate.class)) {
                        LocalDate parse = LocalDate.parse(value.replaceAll("st|rd|nd|th", ""),
                                DateTimeFormatter.ofPattern(annotation.dateMask()));
                        field.set(model, parse);
                    } else if (field.getType().equals(Double.class)) {
                        try {
                            Double parse = DecimalFormat.getCurrencyInstance(Locale.forLanguageTag(annotation.salaryLocale()))
                                    .parse(value).doubleValue();
                            field.set(model, parse);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        field.set(model, value);
                    }
                }
            }
            result.add((T) model);
        }
        return result;

    }


}
