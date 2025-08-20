package com.example;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Пожалуйста, укажите путь к файлу tickets.json");
            return;
        }

        String filePath = args[0];
        if (!Files.exists(Paths.get(filePath))) {
            System.out.println("Файл не найден: " + filePath);
            return;
        }

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(filePath));
            TicketsWrapper wrapper = gson.fromJson(reader, TicketsWrapper.class);

            List<Ticket> filteredTickets = wrapper.getTickets().stream()
                    .filter(t -> "VVO".equals(t.getOrigin()) && "TLV".equals(t.getDestination()))
                    .collect(Collectors.toList());

            if (filteredTickets.isEmpty()) {
                System.out.println("Билеты по маршруту Владивосток - Тель-Авив не найдены.");
                return;
            }

            calculateMinFlightTime(filteredTickets);
            calculatePriceDifference(filteredTickets);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculateMinFlightTime(List<Ticket> tickets) {
        Map<String, Duration> minFlightTimes = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

        for (Ticket ticket : tickets) {
            String departureString = ticket.getDeparture_date() + " " + ticket.getDeparture_time();
            String arrivalString = ticket.getArrival_date() + " " + ticket.getArrival_time();

            LocalDateTime departureDateTime = LocalDateTime.parse(departureString, formatter);
            LocalDateTime arrivalDateTime = LocalDateTime.parse(arrivalString, formatter);

            Duration duration = Duration.between(departureDateTime, arrivalDateTime);

            minFlightTimes.merge(ticket.getCarrier(), duration, (d1, d2) -> d1.compareTo(d2) < 0 ? d1 : d2);
        }

        System.out.println("Минимальное время полета между Владивостоком и Тель-Авивом для каждого авиаперевозчика:");
        minFlightTimes.forEach((carrier, duration) -> {
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            System.out.printf("- %s: %d ч %d мин\n", carrier, hours, minutes);
        });
    }

    private static void calculatePriceDifference(List<Ticket> tickets) {
        List<Double> prices = tickets.stream().map(Ticket::getPrice).collect(Collectors.toList());
        Collections.sort(prices);

        double averagePrice = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double medianPrice;
        int size = prices.size();
        if (size % 2 == 0) {
            medianPrice = (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0;
        } else {
            medianPrice = prices.get(size / 2);
        }

        double difference = averagePrice - medianPrice;

        System.out.println("\nРазница между средней ценой и медианой для полета между Владивостоком и Тель-Авивом:");
        System.out.printf("Средняя цена: %.2f\n", averagePrice);
        System.out.printf("Медианная цена: %.2f\n", medianPrice);
        System.out.printf("Разница: %.2f\n", difference);
    }
}