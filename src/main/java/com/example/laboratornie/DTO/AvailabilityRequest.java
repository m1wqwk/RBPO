package com.example.laboratornie.DTO;

import lombok.Data;
import java.util.List;

@Data
public class AvailabilityRequest {
    private List<Long> roomIds;
    private boolean available;
}