package com.provectus.edu.tests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserModel {

    @ElementLink(locator = "xpath=.//td[1]")
    private String firstName;
    @ElementLink(locator = "xpath=.//td[2]")
    private String lastName;
    @ElementLink(locator = "xpath=.//td[3]")
    private String position;
    @ElementLink(locator = "xpath=.//td[4]")
    private String office;
    @ElementLink(locator = "xpath=.//td[5]")
    private LocalDate startDate;
    @ElementLink(locator = "xpath=.//td[6]")
    private Double salary;
}
