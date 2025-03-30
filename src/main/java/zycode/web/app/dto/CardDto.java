package zycode.web.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CardDto {
    private String type;
    private double balance;
    private double creditLine;
    private String billingAddress;
}
