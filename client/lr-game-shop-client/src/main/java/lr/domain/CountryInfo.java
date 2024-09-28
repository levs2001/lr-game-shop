package lr.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Information about game for exactly one country")
public record CountryInfo(String title, String description, double price) {
    public static CountryInfo create(String title, String description, double price) {
        return new CountryInfo(title, description, price);
    }
}
