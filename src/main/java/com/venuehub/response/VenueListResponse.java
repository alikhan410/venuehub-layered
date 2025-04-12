package com.venuehub.response;

import com.venuehub.dto.VenueDto;

import java.util.List;

public record VenueListResponse(List<VenueDto> venueList) {
}
