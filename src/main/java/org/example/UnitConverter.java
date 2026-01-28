package org.example;

public class UnitConverter {
    public static double convertToKg(String ingredientName, double quantity, UnitType unit) {
        if (unit == UnitType.KG) return quantity;

        switch (ingredientName) {
            case "Tomate":
                if (unit == UnitType.PCS) return quantity / 10.0;
                break;
            case "Laitue":
                if (unit == UnitType.PCS) return quantity / 2.0;
                break;
            case "Chocolat":
                if (unit == UnitType.PCS) return quantity / 10.0;
                if (unit == UnitType.L) return quantity / 2.5;
                break;
            case "Poulet":
                if (unit == UnitType.PCS) return quantity / 8.0;
                break;
            case "Beurre":
                if (unit == UnitType.PCS) return quantity / 4.0;
                if (unit == UnitType.L) return quantity / 5.0;
                break;
        }
        throw new IllegalArgumentException("Conversion impossible pour " + ingredientName + " en " + unit);
    }
}
