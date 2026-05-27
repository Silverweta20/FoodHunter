package com.innovacionti.service.seed;

import com.innovacionti.domain.MenuItem;
import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.User;
import com.innovacionti.service.ReservationService;
import com.innovacionti.service.RestaurantService;
import com.innovacionti.service.ReviewService;
import com.innovacionti.service.UserService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DataSeeder {
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;
    private final ReservationService reservationService;
    private final AtomicBoolean seeded = new AtomicBoolean(false);

    public DataSeeder(UserService userService,
                      RestaurantService restaurantService,
                      ReviewService reviewService,
                      ReservationService reservationService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
        this.reservationService = reservationService;
    }

    @PostConstruct
    public void seed() {
        if (!seeded.compareAndSet(false, true)) {
            return;
        }

        User student1 = new User("ana@uni.edu.co", "123456", "Ana", true, true, "STUDENT");
        User student2 = new User("leo@universidad.edu.co", "123456", "Leo", true, true, "STUDENT");
        User restaurantManager1 = new User("adelwen@restaurant.com", "123456", "Adelwen Admin", true, false, "RESTAURANT");
        User restaurantManager2 = new User("bella@restaurant.com", "123456", "La Bella Admin", true, false, "RESTAURANT");
        User restaurantManager3 = new User("bistro@restaurant.com", "123456", "Bistro Admin", true, false, "RESTAURANT");

        userService.save(student1);
        userService.save(student2);
        userService.save(restaurantManager1);
        userService.save(restaurantManager2);
        userService.save(restaurantManager3);

        Restaurant r1 = new Restaurant();
        r1.setName("Adelwen");
        r1.setCuisine("Italiana");
        r1.setLocation("Centro - Bogotá");
        r1.setDescription("Restaurante especializado en comida italiana con ambiente acogedor");
        r1.setHeroImageUrl("https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&w=1200&q=80");
        r1.setMenuBoardImageUrl("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1200&q=80");
        r1.setMenuBoardPublishedAt(LocalDateTime.now().minusHours(2));
        r1.setDistanceKm(0.8);
        r1.setRankingPosition(1);
        r1.setExecutiveLunchPrice(10000);
        r1.setAvailable(true);
        r1.setOpenHours("07:00 - 23:00");
        r1.setManagerEmail("adelwen@restaurant.com");
        r1.getMenuItems().add(menu(0, 0, "Pizza Margherita", "Masa artesanal con tomate y albahaca", 10000,
                "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=900&q=80",
                "Almuerzo", "12:00 - 16:00", 8, LocalDateTime.now().minusHours(3), true));
        r1.getMenuItems().add(menu(0, 0, "Pasta Carbonara", "Salsa cremosa con panceta", 12000,
                "https://images.unsplash.com/photo-1608219992759-8d74ed8d9f3e?auto=format&fit=crop&w=900&q=80",
                "Almuerzo", "12:00 - 16:00", 6, LocalDateTime.now().minusHours(3), true));
        r1.getMenuItems().add(menu(0, 0, "Risotto Funghi", "Risotto cremoso con champiñones", 14000,
                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=900&q=80",
                "Cena", "18:00 - 23:00", 5, LocalDateTime.now().minusHours(3), false));
        restaurantService.save(r1);

        Restaurant r2 = new Restaurant();
        r2.setName("La Bella Vista");
        r2.setCuisine("Mediterránea");
        r2.setLocation("Chapinero - Bogotá");
        r2.setDescription("Opciones frescas y platos ejecutivos con excelente vista");
        r2.setHeroImageUrl("https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?auto=format&fit=crop&w=1200&q=80");
        r2.setMenuBoardImageUrl("https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=1200&q=80");
        r2.setMenuBoardPublishedAt(LocalDateTime.now().minusHours(1));
        r2.setDistanceKm(1.4);
        r2.setRankingPosition(2);
        r2.setExecutiveLunchPrice(14000);
        r2.setAvailable(true);
        r2.setOpenHours("08:00 - 22:00");
        r2.setManagerEmail("bella@restaurant.com");
        r2.getMenuItems().add(menu(0, 0, "Bowl Mediterráneo", "Vegetales asados, hummus y arroz", 14000,
                "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=900&q=80",
                "Almuerzo", "12:00 - 16:00", 10, LocalDateTime.now().minusHours(2), true));
        r2.getMenuItems().add(menu(0, 0, "Wrap de Pollo", "Pollo grillado con vegetales", 11000,
                "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?auto=format&fit=crop&w=900&q=80",
                "Desayuno", "07:00 - 11:00", 7, LocalDateTime.now().minusHours(2), false));
        r2.getMenuItems().add(menu(0, 0, "Filete del Día", "Acompañado de ensalada", 18000,
                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=900&q=80",
                "Cena", "18:00 - 23:00", 4, LocalDateTime.now().minusHours(2), false));
        restaurantService.save(r2);

        Restaurant r3 = new Restaurant();
        r3.setName("Bistro U");
        r3.setCuisine("Casual");
        r3.setLocation("Salitre - Bogotá");
        r3.setDescription("Ideal para almuerzos rápidos y reservas previas");
        r3.setHeroImageUrl("https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=80");
        r3.setMenuBoardImageUrl("https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&w=1200&q=80");
        r3.setMenuBoardPublishedAt(LocalDateTime.now().minusMinutes(90));
        r3.setDistanceKm(2.1);
        r3.setRankingPosition(3);
        r3.setExecutiveLunchPrice(18000);
        r3.setAvailable(true);
        r3.setOpenHours("07:30 - 21:30");
        r3.setManagerEmail("bistro@restaurant.com");
        r3.getMenuItems().add(menu(0, 0, "Hamburguesa Premium", "Carne angus con papas", 18000,
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=80",
                "Almuerzo", "12:00 - 16:00", 6, LocalDateTime.now().minusHours(1), true));
        r3.getMenuItems().add(menu(0, 0, "Sopa del Día", "Receta casera del chef", 9000,
                "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=900&q=80",
                "Desayuno", "07:00 - 11:00", 12, LocalDateTime.now().minusHours(1), false));
        r3.getMenuItems().add(menu(0, 0, "Lomo al wok", "Vegetales salteados", 17000,
                "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=900&q=80",
                "Cena", "18:00 - 23:00", 5, LocalDateTime.now().minusHours(1), false));
        restaurantService.save(r3);

        Restaurant r4 = new Restaurant();
        r4.setName("Sazón Campus");
        r4.setCuisine("Colombiana");
        r4.setLocation("Usaquén - Bogotá");
        r4.setDescription("Comida tradicional con menú ejecutivo accesible");
        r4.setHeroImageUrl("https://images.unsplash.com/photo-1482049016688-2d3e1b311543?auto=format&fit=crop&w=1200&q=80");
        r4.setMenuBoardImageUrl("https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=80");
        r4.setMenuBoardPublishedAt(LocalDateTime.now().minusHours(4));
        r4.setDistanceKm(3.0);
        r4.setRankingPosition(4);
        r4.setExecutiveLunchPrice(10000);
        r4.setAvailable(true);
        r4.setOpenHours("06:30 - 20:00");
        r4.setManagerEmail("sazon@restaurant.com");
        r4.getMenuItems().add(menu(0, 0, "Corrientazo", "Principio, seco y bebida", 10000,
                "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=900&q=80",
                "Almuerzo", "12:00 - 15:30", 20, LocalDateTime.now().minusHours(5), true));
        restaurantService.save(r4);

        Restaurant r5 = new Restaurant();
        r5.setName("Nómada Bowl");
        r5.setCuisine("Saludable");
        r5.setLocation("Teusaquillo - Bogotá");
        r5.setDescription("Bowls frescos y opciones ligeras para estudiantes");
        r5.setHeroImageUrl("https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=1200&q=80");
        r5.setMenuBoardImageUrl("https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=80");
        r5.setMenuBoardPublishedAt(LocalDateTime.now().minusMinutes(70));
        r5.setDistanceKm(1.1);
        r5.setRankingPosition(5);
        r5.setExecutiveLunchPrice(14000);
        r5.setAvailable(true);
        r5.setOpenHours("08:00 - 21:00");
        r5.setManagerEmail("nomada@restaurant.com");
        r5.getMenuItems().add(menu(0, 0, "Bowl Fit", "Proteína + vegetales + granos", 14000,
                "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=900&q=80",
                "Almuerzo", "12:00 - 16:00", 9, LocalDateTime.now().minusMinutes(70), true));
        restaurantService.save(r5);

        Restaurant r6 = new Restaurant();
        r6.setName("Wok Express");
        r6.setCuisine("Asiática");
        r6.setLocation("Parque Simón Bolívar");
        r6.setDescription("Woks y platos rápidos para llevar");
        r6.setHeroImageUrl("https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=1200&q=80");
        r6.setMenuBoardImageUrl("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1200&q=80");
        r6.setMenuBoardPublishedAt(LocalDateTime.now().minusMinutes(40));
        r6.setDistanceKm(4.5);
        r6.setRankingPosition(6);
        r6.setExecutiveLunchPrice(18000);
        r6.setAvailable(true);
        r6.setOpenHours("11:00 - 22:30");
        r6.setManagerEmail("wok@restaurant.com");
        r6.getMenuItems().add(menu(0, 0, "Wok de Pollo", "Fideos, vegetales y salsa oriental", 18000,
                "https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=900&q=80",
                "Almuerzo", "12:00 - 16:00", 5, LocalDateTime.now().minusMinutes(40), true));
        restaurantService.save(r6);

        reviewService.addReview(student1, r1.getId(), 5, "Excelente lugar, rápido y delicioso.", List.of(
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=900&q=80"));
        reviewService.addReview(student2, r1.getId(), 4, "Muy bueno y el menú ejecutivo vale la pena.", List.of());
        reviewService.addReview(student1, r2.getId(), 5, "La vista y el bowl son buenísimos.", List.of(
                "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=900&q=80"));
        reviewService.addReview(student2, r3.getId(), 4, "Buena opción para almuerzo, servicio rápido.", List.of());

        reservationService.reserve(student1, r1, r1.getMenuItems().get(0), LocalDateTime.now().plusHours(2));
    }

    private MenuItem menu(long id, long restaurantId, String name, String description, int price,
                          String imageUrl, String mealType, String timeRange, int availableUnits,
                          LocalDateTime publishedAt, boolean featured) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(id);
        menuItem.setRestaurantId(restaurantId);
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setImageUrl(imageUrl);
        menuItem.setMealType(mealType);
        menuItem.setTimeRange(timeRange);
        menuItem.setAvailableUnits(availableUnits);
        menuItem.setPublishedAt(publishedAt);
        menuItem.setFeatured(featured);
        return menuItem;
    }
}
