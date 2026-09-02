package com.zeptopluse.service;

import com.zeptopluse.dto.*;
import com.zeptopluse.entity.*;
import com.zeptopluse.exception.*;
import com.zeptopluse.mapper.OrderMapper;
import com.zeptopluse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("499.00");
    private static final BigDecimal STANDARD_DELIVERY_FEE = new BigDecimal("39.00");
    private final CustomerRepository customers;
    private final CustomerAddressRepository addresses;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final OrderMapper mapper;

    @Transactional
    public OrderResponse create(OrderRequest request) {
        if (request.idempotencyKey() != null && !request.idempotencyKey().isBlank()) {
            Optional<CustomerOrder> existing = orders.findByIdempotencyKey(request.idempotencyKey().trim());
            if (existing.isPresent()) return mapper.toResponse(existing.get());
        }
        Customer customer = customers.findById(request.customerId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.customerId()));
        CustomerAddress address = resolveAddress(customer, request);
        Map<Long, OrderItemRequest> requested = uniqueItems(request.items());
        Map<Long, Product> locked = lockProducts(requested.keySet());
        validateAvailability(requested, locked);

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setOrderNumber("ZP-" + UUID.randomUUID().toString().toUpperCase(Locale.ROOT));
        order.setIdempotencyKey(request.idempotencyKey() == null || request.idempotencyKey().isBlank() ? null : request.idempotencyKey().trim());
        PaymentMethod method = request.paymentMethod() == null ? PaymentMethod.COD : request.paymentMethod();
        order.setStatus(method == PaymentMethod.ONLINE ? OrderStatus.PAYMENT_PENDING : OrderStatus.CREATED);
        order.setPaymentMethod(method);
        order.setPaymentStatus(method == PaymentMethod.ONLINE ? PaymentStatus.CREATED : PaymentStatus.AUTHORIZED);
        order.setAddressSnapshot(addressSnapshot(address));
        order.setDiscountAmount(ZERO);

        BigDecimal subtotal = addAuthoritativeItems(order, requested, locked);
        order.setDeliveryFee(deliveryFee(subtotal));
        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal.add(order.getDeliveryFee()).subtract(order.getDiscountAmount()));
        return mapper.toResponse(orders.save(order));
    }

    public List<OrderResponse> list(Long customerId) {
        if (!customers.existsById(customerId)) throw new ResourceNotFoundException("Customer not found: " + customerId);
        return orders.findByCustomerIdOrderByCreatedAtDesc(customerId).stream().map(mapper::toResponse).toList();
    }

    public OrderResponse get(Long id) {
        return mapper.toResponse(orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)));
    }

    @Transactional
    public OrderResponse markPaymentCaptured(Long orderId) {
        CustomerOrder order = orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (order.getPaymentMethod() != PaymentMethod.ONLINE) throw new IllegalArgumentException("Order is not an online payment order");
        order.setPaymentStatus(PaymentStatus.CAPTURED);
        order.setStatus(OrderStatus.CREATED);
        return mapper.toResponse(order);
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        CustomerOrder order = orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        if (EnumSet.of(OrderStatus.PACKED, OrderStatus.DELIVERY_ASSIGNED, OrderStatus.PICKED_UP, OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED).contains(order.getStatus())) {
            throw new ConflictException("This order can no longer be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(order.getPaymentMethod() == PaymentMethod.ONLINE && order.getPaymentStatus() == PaymentStatus.CAPTURED ? PaymentStatus.REFUNDED : PaymentStatus.CANCELLED);
        return mapper.toResponse(order);
    }

    private CustomerAddress resolveAddress(Customer customer, OrderRequest request) {
        if (request.addressId() != null) {
            return addresses.findByIdAndCustomerId(request.addressId(), request.customerId()).orElseThrow(() -> new ResourceNotFoundException("Address not found for customer"));
        }
        if (request.address() == null) throw new IllegalArgumentException("addressId or address is required");
        CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        AddressRequest a = request.address();
        address.setLabel(a.label() == null || a.label().isBlank() ? "Home" : a.label());
        address.setRecipientName(a.recipientName());
        address.setPhone(a.phone());
        address.setLine1(a.line1());
        address.setLine2(a.line2());
        address.setLandmark(a.landmark());
        address.setCity(a.city());
        address.setState(a.state());
        address.setPostalCode(a.postalCode());
        address.setLatitude(a.latitude());
        address.setLongitude(a.longitude());
        address.setDefaultAddress(a.defaultAddress());
        return addresses.save(address);
    }

    private Map<Long, OrderItemRequest> uniqueItems(List<OrderItemRequest> items) {
        Map<Long, OrderItemRequest> requested = new LinkedHashMap<>();
        for (OrderItemRequest item : items) {
            if (requested.putIfAbsent(item.productId(), item) != null) throw new IllegalArgumentException("A product can appear only once in an order");
        }
        return requested;
    }

    private Map<Long, Product> lockProducts(Set<Long> ids) {
        Map<Long, Product> locked = new HashMap<>();
        ids.stream().sorted().forEach(id -> locked.put(id, products.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id))));
        return locked;
    }

    private void validateAvailability(Map<Long, OrderItemRequest> requested, Map<Long, Product> locked) {
        for (OrderItemRequest item : requested.values()) {
            Product product = locked.get(item.productId());
            if (!product.isAvailability()) throw new ConflictException("Product is unavailable: " + product.getName());
            if (product.getStockQuantity() < item.quantity()) throw new ConflictException("Insufficient inventory for: " + product.getName());
        }
    }

    private BigDecimal addAuthoritativeItems(CustomerOrder order, Map<Long, OrderItemRequest> requested, Map<Long, Product> locked) {
        BigDecimal subtotal = ZERO;
        for (OrderItemRequest requestedItem : requested.values()) {
            Product product = locked.get(requestedItem.productId());
            BigDecimal lineTotal = product.getSellingPrice().multiply(BigDecimal.valueOf(requestedItem.quantity()));
            OrderItem item = new OrderItem();
            item.setProductId(product.getId()); item.setProductSku(product.getSku()); item.setProductName(product.getName()); item.setProductBrand(product.getBrand()); item.setProductImageUrl(product.getImageUrl()); item.setProductUnit(product.getUnit()); item.setQuantity(requestedItem.quantity()); item.setUnitPrice(product.getSellingPrice()); item.setLineTotal(lineTotal);
            order.addItem(item);
            product.setStockQuantity(product.getStockQuantity() - requestedItem.quantity());
            subtotal = subtotal.add(lineTotal);
        }
        return subtotal;
    }

    private BigDecimal deliveryFee(BigDecimal subtotal) {
        return subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ? ZERO : STANDARD_DELIVERY_FEE;
    }

    private String addressSnapshot(CustomerAddress address) {
        return String.join(", ", List.of(address.getRecipientName(), address.getLine1(), blank(address.getLine2()), blank(address.getLandmark()), address.getCity(), address.getState() + " " + address.getPostalCode(), address.getPhone()).stream().filter(value -> !value.isBlank()).toList());
    }
    private String blank(String value) { return value == null ? "" : value; }
}
