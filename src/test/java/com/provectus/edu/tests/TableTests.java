package com.provectus.edu.tests;

import com.codeborne.selenide.SelenideElement;
import com.microsoft.playwright.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.*;


public class TableTests {
    Playwright playwright = Playwright.create();
    Page page;

    @BeforeClass
    public void beforeClass() {
        page = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false))
                .newPage();
        page.navigate("https://datatables.net/examples/server_side/simple.html");
        page.selectOption("[name=example_length]", "100");
        page.waitForTimeout(2000);

        open("https://datatables.net/examples/server_side/simple.html");
        $(byName("example_length")).selectOption("100");
        sleep(2000);
    }

    @Test
    public void tablesWithSelenide() throws ParseException {
        List<Map<String, Object>> result = new ArrayList<>();

        SelenideElement table = $("#example");
        for (SelenideElement row : table.$$("tbody tr")) {
            List<String> texts = row.$$("td").texts();
            result.add(textsToMap(texts));
        }
        result.forEach(System.out::println);
    }

    @Test
    public void tableWithPlaywright() throws ParseException {
        List<Map<String, Object>> result = new ArrayList<>();

        Locator table = page.locator("#example");
        Locator rows = table.locator("tbody tr");

        for (int i = 0; i < rows.count(); i++) {
            List<String> texts = rows.nth(i).locator("td").allTextContents();
            result.add(textsToMap(texts));
        }

        result.forEach(System.out::println);
        playwright.close();
    }

    @Test
    public void tableWithJSoup() {
        List<Map<String, Object>> result = new ArrayList<>();

        SelenideElement table = $("#example");
        String tableAsString = table.attr("outerHTML");
        Document parse = Jsoup.parse(tableAsString);
        for (Element element : parse.select("tbody tr")) {
            List<String> texts = element.select("td").eachText();
            result.add(textsToMap(texts));
        }
        result.forEach(System.out::println);
    }

    @Test
    public void tableUsingUtilMethodPlaywright() {
        Locator table = page.locator("#example");
        List<UserModel> result = TableUtils.getTable(table, "tbody tr", UserModel.class);
        System.out.println(result);
    }

    @Test
    public void tableUsingUtilMethodSelenide() {
        var table = $("#example");
        List<UserModel> result = TableUtils.getTable(table, "tbody tr", UserModel.class);
        System.out.println(result);
    }


    public Map<String, Object> textsToMap(List<String> texts) {
        Map<String, Object> rowMap = new HashMap<>();
        String firstName = texts.get(0);
        String lastName = texts.get(1);
        String position = texts.get(2);
        String office = texts.get(3);
        String startDateAsString = texts.get(4);
        LocalDate startDate = LocalDate.parse(startDateAsString.replaceAll("st|rd|nd|th", ""), DateTimeFormatter.ofPattern("d MMM yy"));
        String salaryAsString = texts.get(5);
        Double salary = null;
        try {
            salary = DecimalFormat.getCurrencyInstance(Locale.US).parse(salaryAsString).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        rowMap.put("firstName", firstName);
        rowMap.put("lastName", lastName);
        rowMap.put("position", position);
        rowMap.put("office", office);
        rowMap.put("startDate", startDate);
        rowMap.put("salary", salary);
        return rowMap;
    }

    public List<Locator> getAllElements(Locator locator) {
        return IntStream.range(0, locator.count()).mapToObj(locator::nth).toList();
    }
}
