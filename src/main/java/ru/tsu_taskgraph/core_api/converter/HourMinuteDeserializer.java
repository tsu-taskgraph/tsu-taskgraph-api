package ru.tsu_taskgraph.core_api.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;

public class HourMinuteDeserializer extends JsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.isBlank()) {
            return null;
        }

        if (value.contains(":")) {
            String[] parts = value.split(":");
            if (parts.length != 2) {
                throw new InvalidFormatException(p,
                        "Неверный формат времени. Ожидается 'ЧЧ:ММ' (например, '1:30') или число (например, 1.5).",
                        value, Double.class);
            }
            try {
                double hours = Double.parseDouble(parts[0]);
                double minutes = Double.parseDouble(parts[1]);
                if (minutes < 0 || minutes >= 60) {
                    throw new InvalidFormatException(p, "Минуты должны быть в диапазоне от 0 до 59.", value, Double.class);
                }
                return hours + (minutes / 60.0);
            } catch (NumberFormatException e) {
                throw new InvalidFormatException(p,
                        "Неверный формат чисел в строке времени. Часы и минуты должны быть числами.",
                        value, Double.class);
            }
        } else {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new InvalidFormatException(p,
                        "Неверный формат числа. Ожидается число (например, 1.5) или время в формате 'ЧЧ:ММ'.",
                        value, Double.class);
            }
        }
    }
}