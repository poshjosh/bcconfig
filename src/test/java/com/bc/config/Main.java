/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bc.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2018 2:20:01 PM
 */
public class Main {

    public static void main(String [] args) throws ParseException {

        System.out.println("LocalDateTime.now()" + LocalDateTime.now());

        final String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        final String text = "2018-05-11T14:27:59+0100";
        System.out.println("Pattern: " + pattern);
        System.out.println("Text: " + text);

        final SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setLenient(true);
        sdf.applyPattern(pattern);
        final Date date = sdf.parse(text);
        System.out.println("Date: " + date);

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        final LocalDateTime ldt = LocalDateTime.parse(text, dtf);
        System.out.println("Local date time: " + ldt);
    }
}
