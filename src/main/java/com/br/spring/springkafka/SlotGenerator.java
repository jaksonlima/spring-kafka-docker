package com.br.spring.springkafka;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SlotGenerator {
    public static void main(String[] args) {
        // Define as datas
        LocalDateTime startDate1 = LocalDateTime.of(2023, 7, 6, 8, 0);
        LocalDateTime endDate1 = LocalDateTime.of(2023, 7, 6, 9, 0);

        LocalDateTime startDate2 = LocalDateTime.of(2023, 7, 7, 8, 0);
        LocalDateTime endDate2 = LocalDateTime.of(2023, 7, 7, 9, 0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Gera os slots de 30 minutos para o dia 06/07/2023
        generateSlots(startDate1, endDate1, formatter);

        // Gera os slots de 30 minutos para o dia 07/07/2023
        generateSlots(startDate2, endDate2, formatter);
    }

    private static void generateSlots(LocalDateTime startDate, LocalDateTime endDate, DateTimeFormatter formatter) {
        while (startDate.isBefore(endDate)) {
            LocalDateTime endOfSlot = startDate.plusMinutes(30);

            // Imprime ou faça o que precisar com os slots
            System.out.println("Slot: " + startDate.format(formatter) + " - " + endOfSlot.format(formatter));

            startDate = endOfSlot;
        }
    }
}
