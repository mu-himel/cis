package com.aes.erp.user_management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

import static com.aes.erp.config.RegexPattern.ALPHABET_ONLY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Pattern(regexp = ALPHABET_ONLY, message = "Privilege Name: Field cannot have numeric or special characters")
    private String privilegeName;
}
