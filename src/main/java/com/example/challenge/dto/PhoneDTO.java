package com.example.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PhoneDTO {
    private Long number;
    private Integer cityCode;
    private String countryCode;
}
