package com.micompany.ecommerce.dto.orders;

import com.micompany.ecommerce.models.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateDto {

    @NotNull(message = "The status is mandatory")
    private Status status;
}
