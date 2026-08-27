package com.zeptopluse.mapper;

import com.zeptopluse.dto.*;
import com.zeptopluse.entity.CustomerAddress;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressResponse toResponse(CustomerAddress address) {
        return new AddressResponse(address.getId(), address.getCustomer().getId(), address.getLabel(), address.getRecipientName(), address.getPhone(), address.getLine1(), address.getLine2(), address.getLandmark(), address.getCity(), address.getState(), address.getPostalCode(), address.getLatitude(), address.getLongitude(), address.isDefaultAddress());
    }
    public void update(CustomerAddress address, AddressRequest request) {
        address.setLabel(request.label()); address.setRecipientName(request.recipientName()); address.setPhone(request.phone()); address.setLine1(request.line1()); address.setLine2(request.line2()); address.setLandmark(request.landmark()); address.setCity(request.city()); address.setState(request.state()); address.setPostalCode(request.postalCode()); address.setLatitude(request.latitude()); address.setLongitude(request.longitude()); address.setDefaultAddress(request.defaultAddress());
    }
}
