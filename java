package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;

public class ConversorMonedas {
    private static final String API_KEY = "16996726b18ab3a37d5bd573";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";
    private static Map<String, Double> tasasCambio = new HashMap<>();

    public static void main(String[] args) {
        actualizarTasasCambio();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nConversor de Monedas");
            System.out.println("1. Realizar conversión");
            System.out.println("2. Actualizar tasas de cambio");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            switch (opcion) {
                case 1:
                    realizarConversion(scanner);
                    break;
                case 2:
                    actualizarTasasCambio();
                    System.out.println("Tasas de cambio actualizadas.");
                    break;
                case 3:
                    System.out.println("Gracias por usar el conversor de monedas. ¡Hasta luego!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
    }

    private static void realizarConversion(Scanner scanner) {
        System.out.print("Ingrese la cantidad: ");
        double cantidad = scanner.nextDouble();
        scanner.nextLine(); // Consumir el salto de línea

        System.out.print("Ingrese la moneda de origen (USD, EUR, GBP, JPY, etc.): ");
        String monedaOrigen = scanner.nextLine().toUpperCase();

        System.out.print("Ingrese la moneda de destino (USD, EUR, GBP, JPY, etc.): ");
        String monedaDestino = scanner.nextLine().toUpperCase();

        try {
            double resultado = convertir(cantidad, monedaOrigen, monedaDestino);
            System.out.printf("%.2f %s = %.2f %s%n", cantidad, monedaOrigen, resultado, monedaDestino);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void actualizarTasasCambio() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONObject json = new JSONObject(content.toString());
            JSONObject rates = json.getJSONObject("conversion_rates");

            tasasCambio.clear();
            for (String key : rates.keySet()) {
                tasasCambio.put(key, rates.getDouble(key));
            }

            System.out.println("Tasas de cambio actualizadas correctamente.");
        } catch (Exception e) {
            System.out.println("Error al actualizar las tasas de cambio: " + e.getMessage());
        }
    }

    private static double convertir(double cantidad, String monedaOrigen, String monedaDestino) {
        if (!tasasCambio.containsKey(monedaOrigen) || !tasasCambio.containsKey(monedaDestino)) {
            throw new IllegalArgumentException("Moneda no soportada");
        }
        
        double tasaOrigen = tasasCambio.get(monedaOrigen);
        double tasaDestino = tasasCambio.get(monedaDestino);
        
        return cantidad * (tasaDestino / tasaOrigen);
    }
}
