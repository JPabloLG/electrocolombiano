package co.uniquindio.bd.electrocolombiano.model;

import lombok.*;
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Payment {

    private  String payMentId;
    private  float totalPrice;
    private  boolean isCredit;

}
