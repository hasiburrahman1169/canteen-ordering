package com.canteen.ordering.web;

import com.canteen.ordering.menu.MenuItem;
import com.canteen.ordering.menu.MenuItemRepository;
import com.canteen.ordering.order.OrderEntity;
import com.canteen.ordering.order.OrderLine;
import com.canteen.ordering.order.OrderRepository;
import com.canteen.ordering.order.OrderStatus;
import com.canteen.ordering.order.PaymentMethod;
import com.canteen.ordering.order.PaymentStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class HomeController {
    private final MenuItemRepository menuItems;
    private final OrderRepository orders;

    public HomeController(MenuItemRepository menuItems, OrderRepository orders) {
        this.menuItems = menuItems;
        this.orders = orders;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("menuItems", menuItems.findAll());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam Map<String, String> form, Model model) {
        OrderEntity order = new OrderEntity();
        order.setCustomerName(required(form, "customerName"));
        order.setPhone(required(form, "phone"));
        order.setPickupTime(LocalDateTime.parse(required(form, "pickupTime")));
        PaymentMethod paymentMethod = PaymentMethod.valueOf(required(form, "paymentMethod"));
        order.setPaymentMethod(paymentMethod);
        if (paymentMethod == PaymentMethod.CASH_ON_PICKUP) {
            order.setPaymentStatus(PaymentStatus.UNPAID);
        } else {
            order.setPaymentReference(required(form, "paymentReference"));
            order.setPaymentStatus(PaymentStatus.PENDING_VERIFICATION);
        }

        menuItems.findAll().forEach(item -> {
            String rawQuantity = form.get("quantity_" + item.getId());
            if (rawQuantity != null && !rawQuantity.isBlank() && Integer.parseInt(rawQuantity) > 0) {
                int quantity = Integer.parseInt(rawQuantity);
                if (!item.isAvailable() || quantity > item.getStock()) {
                    throw new IllegalArgumentException(item.getName() + " is not available in that quantity.");
                }
                order.addLine(new OrderLine(item.getId(), item.getName(), item.getPrice(), quantity));
                item.setStock(item.getStock() - quantity);
                menuItems.save(item);
            }
        });
        if (order.getLines().isEmpty()) {
            model.addAttribute("error", "কমপক্ষে একটি খাবার নির্বাচন করুন।");
            return home(model);
        }
        OrderEntity saved = orders.save(order);
        return "redirect:/order/" + saved.getId();
    }

    @GetMapping("/order/{id}")
    public String order(@PathVariable Long id, Model model) {
        model.addAttribute("order", orders.findById(id).orElseThrow());
        return "order";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("orders", orders.findAllByOrderByCreatedAtDesc());
        model.addAttribute("menuItems", menuItems.findAll());
        model.addAttribute("todaySales", orders.findByCreatedAtBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())
                .stream().map(OrderEntity::getTotal).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        return "admin";
    }

    @PostMapping("/admin/order/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        OrderEntity order = orders.findById(id).orElseThrow();
        order.setStatus(status);
        orders.save(order);
        return "redirect:/admin";
    }

    @PostMapping("/admin/menu")
    public String addMenu(@Valid @ModelAttribute MenuItem item, BindingResult result) {
        if (result.hasErrors()) {
            throw new IllegalArgumentException("Menu item data is invalid.");
        }
        menuItems.save(item);
        return "redirect:/admin";
    }

    @PostMapping("/admin/menu/{id}/availability")
    public String toggleAvailability(@PathVariable Long id) {
        MenuItem item = menuItems.findById(id).orElseThrow();
        item.setAvailable(!item.isAvailable());
        menuItems.save(item);
        return "redirect:/admin";
    }

    private String required(Map<String, String> form, String key) {
        String value = form.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " is required.");
        }
        return value;
    }
}
