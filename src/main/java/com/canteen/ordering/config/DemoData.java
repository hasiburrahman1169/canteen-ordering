package com.canteen.ordering.config;

import com.canteen.ordering.menu.MenuItem;
import com.canteen.ordering.menu.MenuItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DemoData {
    @Bean
    CommandLineRunner seedMenu(MenuItemRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new MenuItem("Chicken Biryani", "বাসমতি চাল, চিকেন ও সালাদ", new BigDecimal("140"), 30, "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=600"));
                repository.save(new MenuItem("Beef Burger", "চিজ, বিফ প্যাটি ও ফ্রেঞ্চ ফ্রাই", new BigDecimal("180"), 20, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600"));
                repository.save(new MenuItem("Cold Coffee", "ঠান্ডা কফি ও ফোম", new BigDecimal("90"), 25, "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=600"));
            }
        };
    }
}
