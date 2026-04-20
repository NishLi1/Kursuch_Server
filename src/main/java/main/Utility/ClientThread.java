package main.Utility;

import com.google.gson.Gson;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Models.Entities.*;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Models.AnalysisItem;
import main.Services.*;
import java.time.LocalDate;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ClientThread implements Runnable {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Gson gson = new Gson();

    private final UserService userService = new UserService();
    private final UserProfileService userProfileService = new UserProfileService();
    private final ProductService productService = new ProductService();
    private final FoodDiaryService foodDiaryService = new FoodDiaryService();
    private final AnalysisService analysisService = new AnalysisService();
    private final ReportService reportService = new ReportService();


    private User currentUser;

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
    }

    @Override
    public void run() {
        try {
            System.out.println("Клиент подключился: " + socket.getInetAddress());

            while (true) {
                String message = in.readLine();
                if (message == null || message.isEmpty()) break;

                Request request = gson.fromJson(message, Request.class);
                Response response = handleRequest(request);

                out.println(gson.toJson(response));
                out.flush();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обработке клиента " + socket.getInetAddress() + ": " + e.getMessage());
        } finally {
            System.out.println("Клиент " + socket.getInetAddress() + " отключился.");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Главный обработчик всех запросов от клиента
     */
    private Response handleRequest(Request request) {
        try {
            switch (request.getRequestType()) {

                case REGISTER:
                    return handleRegister(request);
                case LOGIN:
                    return handleLogin(request);
                case GET_ALL_USERS:
                    return handleGetAllUsers();
                case UPDATE_PROFILE:
                    return handleUpdateProfile(request);
                case GET_PROFILE:
                    return handleGetProfile(request);
                case SEARCH_PRODUCTS:
                    return handleSearchProducts(request);
                case GET_DIARY:
                    return handleGetDiary(request);
                case ADD_FOOD_ENTRY:
                    return handleAddFoodEntry(request);
                case GET_ANALYSIS:
                    return handleGetAnalysis(request);
                case GENERATE_REPORT:
                    return handleGenerateReport(request);
                case SAVE_PRODUCT:
                    return handleSaveProduct(request);
                case DELETE_PRODUCT:
                    return handleDeleteProduct(request);
                case GET_ALL_PRODUCTS:
                    return handleGetAllProducts();
                case UPDATE_USER_ROLE:
                    return handleUpdateUserRole(request);

                default:
                    return new Response(ResponseStatus.ERROR, "Неизвестный тип запроса", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, e.getMessage(), null);
        }
    }

    private Response handleRegister(Request request) {
        User user = gson.fromJson(request.getRequestMessage(), User.class);

        // Если клиент не передал профиль — создаём пустой
        if (user.getUserProfile() == null) {
            user.setUserProfile(new UserProfile());
        }

        try {
            User registeredUser = userService.register(user, user.getUserProfile());

            // === РАЗРЫВАЕМ ЦИКЛИЧЕСКУЮ ССЫЛКУ ===
            if (registeredUser.getUserProfile() != null) {
                registeredUser.getUserProfile().setUser(null);
            }

            return new Response(ResponseStatus.OK, "Регистрация прошла успешно", gson.toJson(registeredUser));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, e.getMessage(), null);
        }
    }

    private Response handleLogin(Request request) {
        try {
            User loginUser = gson.fromJson(request.getRequestMessage(), User.class);

            User user = userService.login(loginUser.getLogin(), loginUser.getPasswordHash());

            this.currentUser = user;

            user.setUserProfile(null);

            System.out.println("✅ Логин успешен для пользователя: " + user.getLogin());

            return new Response(ResponseStatus.OK, "Авторизация успешна", gson.toJson(user));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Неверный логин или пароль", null);
        }
    }

    private Response handleUpdateProfile(Request request) {
        try {
            UserProfile incomingProfile = gson.fromJson(request.getRequestMessage(), UserProfile.class);

            if (incomingProfile.getUser() == null || incomingProfile.getUser().getId() == 0) {
                return new Response(ResponseStatus.ERROR, "Не указан пользователь для обновления профиля", null);
            }

            UserProfile updated = userProfileService.updateProfile(incomingProfile);

            System.out.println("✅ Профиль пользователя ID=" + incomingProfile.getUser().getId() + " успешно обновлён");

            // Возвращаем только успех — без объекта, чтобы не было StackOverflow
            return new Response(ResponseStatus.OK, "Профиль успешно обновлён", "");

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, e.getMessage(), null);
        }
    }

    private Response handleGetProfile(Request request) {
        try {
            int userId = Integer.parseInt(request.getRequestMessage().trim());

            UserProfile profile = userProfileService.getFullProfileByUserId(userId);

            if (profile == null) {
                return new Response(ResponseStatus.ERROR, "Профиль не найден", null);
            }

            // === Создаём ЧИСТЫЙ объект без Hibernate Proxy ===
            UserProfile cleanProfile = new UserProfile();
            cleanProfile.setId(profile.getId());
            cleanProfile.setDateOfBirth(profile.getDateOfBirth());
            cleanProfile.setGender(profile.getGender());
            cleanProfile.setWeight(profile.getWeight());
            cleanProfile.setHeight(profile.getHeight());
            cleanProfile.setActivityLevel(profile.getActivityLevel());

            // Простой User (только id) — без прокси
            if (profile.getUser() != null) {
                User cleanUser = new User();
                cleanUser.setId(profile.getUser().getId());
                cleanProfile.setUser(cleanUser);
            }

            // Копируем нормы КБЖУ
            if (profile.getNutritionNorms() != null) {
                NutritionNorms norms = profile.getNutritionNorms();
                NutritionNorms cleanNorms = new NutritionNorms();
                cleanNorms.setId(norms.getId());
                cleanNorms.setCalories(norms.getCalories());
                cleanNorms.setProteins(norms.getProteins());
                cleanNorms.setFats(norms.getFats());
                cleanNorms.setCarbs(norms.getCarbs());
                cleanProfile.setNutritionNorms(cleanNorms);
            }

            System.out.println("✅ Профиль успешно загружен для пользователя ID=" + userId);

            return new Response(ResponseStatus.OK, "Профиль загружен", gson.toJson(cleanProfile));

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка загрузки профиля", null);
        }
    }

    // ==================== SEARCH_PRODUCTS ====================
    private Response handleSearchProducts(Request request) {
        try {
            String query = request.getRequestMessage().trim();

            List<Product> products = productService.searchProducts(query);

            // Создаём чистые объекты, но сохраняем нутриенты
            List<Product> cleanProducts = new ArrayList<>();

            for (Product p : products) {
                Product clean = new Product();
                clean.setId(p.getId());
                clean.setName(p.getName());

                if (p.getCategory() != null) {
                    Category cat = new Category();
                    cat.setId(p.getCategory().getId());
                    cat.setName(p.getCategory().getName());
                    clean.setCategory(cat);
                }

                // Копируем нутриенты
                if (p.getNutrients() != null) {
                    List<ProductNutrient> cleanNutrients = new ArrayList<>();
                    for (ProductNutrient pn : p.getNutrients()) {
                        if (pn.getNutrient() != null) {
                            ProductNutrient cleanPn = new ProductNutrient();
                            cleanPn.setAmount(pn.getAmount());

                            Nutrient cleanNutrient = new Nutrient();
                            cleanNutrient.setId(pn.getNutrient().getId());
                            cleanNutrient.setName(pn.getNutrient().getName());
                            cleanNutrient.setUnit(pn.getNutrient().getUnit());
                            cleanPn.setNutrient(cleanNutrient);

                            cleanNutrients.add(cleanPn);
                        }
                    }
                    clean.setNutrients(cleanNutrients);
                }

                cleanProducts.add(clean);
            }

            System.out.println("✅ Найдено " + cleanProducts.size() + " продуктов по запросу: " + query);

            return new Response(ResponseStatus.OK, "Поиск выполнен", gson.toJson(cleanProducts));

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка поиска продуктов", null);
        }
    }

    // ==================== ADD_FOOD_ENTRY ====================
    private Response handleAddFoodEntry(Request request) {
        try {
            if (currentUser == null) {
                return new Response(ResponseStatus.ERROR, "Пользователь не авторизован", null);
            }

            FoodEntry entry = gson.fromJson(request.getRequestMessage(), FoodEntry.class);

            // Правильный вызов с датой
            boolean success = foodDiaryService.addFoodEntry(
                    currentUser.getId(),
                    LocalDate.now(),      // сегодняшняя дата
                    entry
            );

            if (success) {
                System.out.println("✅ Запись в дневник добавлена успешно (пользователь ID=" + currentUser.getId() + ")");
                return new Response(ResponseStatus.OK, "Запись добавлена в дневник", "");
            } else {
                return new Response(ResponseStatus.ERROR, "Не удалось добавить запись", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка при добавлении записи в дневник", null);
        }
    }

    // ==================== GET_DIARY ====================
    private Response handleGetDiary(Request request) {
        try {
            if (currentUser == null) {
                return new Response(ResponseStatus.ERROR, "Пользователь не авторизован", null);
            }

            LocalDate date = LocalDate.parse(request.getRequestMessage().trim());

            List<FoodEntry> entries = foodDiaryService.getDiaryEntries(currentUser.getId(), date);

            List<FoodEntry> cleanEntries = new ArrayList<>();

            for (FoodEntry entry : entries) {
                FoodEntry clean = new FoodEntry();
                clean.setId(entry.getId());
                clean.setWeight(entry.getWeight());

                if (entry.getProduct() != null) {
                    Product p = entry.getProduct();

                    Product cleanProduct = new Product();
                    cleanProduct.setId(p.getId());
                    cleanProduct.setName(p.getName());
                    clean.setProduct(cleanProduct);

                    double weight = entry.getWeight() != null ? entry.getWeight() : 0.0;
                    double factor = weight / 100.0;

                    // Расчёт КБЖУ
                    clean.setCalories(p.getCalories() * factor);
                    clean.setProteins(p.getProteins() * factor);
                    clean.setFats(p.getFats() * factor);
                    clean.setCarbs(p.getCarbs() * factor);
                }

                cleanEntries.add(clean);
            }

            System.out.println("✅ Дневник загружен (" + cleanEntries.size() + " записей)");

            return new Response(ResponseStatus.OK, "Дневник загружен", gson.toJson(cleanEntries));

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка загрузки дневника", null);
        }
    }

    // ==================== GET_ANALYSIS ====================
    private Response handleGetAnalysis(Request request) {
        try {
            if (currentUser == null) {
                return new Response(ResponseStatus.ERROR, "Пользователь не авторизован", null);
            }

            LocalDate date = LocalDate.parse(request.getRequestMessage().trim());

            List<AnalysisItem> analysisItems = analysisService.getDayAnalysisAsItems(currentUser.getId(), date);

            System.out.println("✅ Анализ за " + date + " сформирован (" + analysisItems.size() + " строк)");

            return new Response(ResponseStatus.OK, "Анализ готов", gson.toJson(analysisItems));

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка формирования анализа", null);
        }
    }

    // ==================== GENERATE_REPORT ====================
    private Response handleGenerateReport(Request request) {
        try {
            if (currentUser == null) {
                return new Response(ResponseStatus.ERROR, "Пользователь не авторизован", null);
            }

            // Парсим дату из сообщения
            LocalDate date = LocalDate.parse(request.getRequestMessage().trim());

            String reportText = reportService.generateDailyReport(currentUser.getId(), date);

            System.out.println("✅ Отчёт за " + date + " сформирован для пользователя ID=" + currentUser.getId());

            return new Response(ResponseStatus.OK, "Отчёт успешно сформирован", reportText);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка формирования отчёта: " + e.getMessage(), null);
        }
    }

    /**
     * Получить все продукты (для админки)
     */
    private Response handleGetAllProducts() {
        try {
            if (currentUser == null || currentUser.getRole() == null ||
                    (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName()) &&
                            !"EMPLOYEE".equalsIgnoreCase(currentUser.getRole().getName()))) {
                return new Response(ResponseStatus.ERROR, "Нет прав доступа", null);
            }

            List<Product> products = productService.getAllProducts();

            // Очищаем от Hibernate-прокси
            List<Product> cleanProducts = new ArrayList<>();
            for (Product p : products) {
                Product clean = new Product();
                clean.setId(p.getId());
                clean.setName(p.getName());

                if (p.getCategory() != null) {
                    Category cat = new Category();
                    cat.setId(p.getCategory().getId());
                    cat.setName(p.getCategory().getName());
                    clean.setCategory(cat);
                }

                // Копируем нутриенты
                if (p.getNutrients() != null) {
                    List<ProductNutrient> cleanNutrients = new ArrayList<>();
                    for (ProductNutrient pn : p.getNutrients()) {
                        if (pn.getNutrient() != null) {
                            ProductNutrient cleanPn = new ProductNutrient();
                            cleanPn.setAmount(pn.getAmount());

                            Nutrient cleanNutrient = new Nutrient();
                            cleanNutrient.setId(pn.getNutrient().getId());
                            cleanNutrient.setName(pn.getNutrient().getName());
                            cleanNutrient.setUnit(pn.getNutrient().getUnit());
                            cleanPn.setNutrient(cleanNutrient);

                            cleanNutrients.add(cleanPn);
                        }
                    }
                    clean.setNutrients(cleanNutrients);
                }
                cleanProducts.add(clean);
            }

            return new Response(ResponseStatus.OK, "Список продуктов", gson.toJson(cleanProducts));

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка получения продуктов: " + e.getMessage(), null);
        }
    }

    /**
     * Сохранить/обновить продукт (SAVE_PRODUCT)
     * Принимает JSON объекта Product
     */
    private Response handleSaveProduct(Request request) {
        try {
            if (currentUser == null || currentUser.getRole() == null ||
                    (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName()) &&
                            !"EMPLOYEE".equalsIgnoreCase(currentUser.getRole().getName()))) {
                return new Response(ResponseStatus.ERROR, "Нет прав доступа", null);
            }

            Product product = gson.fromJson(request.getRequestMessage(), Product.class);

            if (product.getId() == 0) {
                // Новый продукт
                productService.addProduct(product);
                return new Response(ResponseStatus.OK, "Продукт успешно добавлен", null);
            } else {
                // Обновление существующего
                productService.updateProduct(product);
                return new Response(ResponseStatus.OK, "Продукт успешно обновлён", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка сохранения продукта: " + e.getMessage(), null);
        }
    }

    /**
     * Удалить продукт (DELETE_PRODUCT)
     * Принимает JSON: {"id": 123}
     */
    private Response handleDeleteProduct(Request request) {
        try {
            if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
                return new Response(ResponseStatus.ERROR, "Только администратор может удалять продукты", null);
            }

            java.util.Map<String, Object> data = gson.fromJson(request.getRequestMessage(),
                    new com.google.gson.reflect.TypeToken<java.util.Map<String, Object>>(){}.getType());

            int productId = ((Double) data.get("id")).intValue();

            productService.deleteProduct(productId);

            return new Response(ResponseStatus.OK, "Продукт успешно удалён", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка удаления продукта: " + e.getMessage(), null);
        }
    }

    // ==================== GET_ALL_USERS ====================
    private Response handleGetAllUsers() {
        try {
            List<User> users = userService.getAllUsers();

            // === ОЧИЩАЕМ объекты от Hibernate-прокси и циклических ссылок ===
            List<User> cleanUsers = new ArrayList<>();
            for (User u : users) {
                User clean = new User();
                clean.setId(u.getId());
                clean.setLogin(u.getLogin());
                clean.setEmail(u.getEmail());

                // Только имя роли (без объекта)
                if (u.getRole() != null) {
                    Role cleanRole = new Role();
                    cleanRole.setId(u.getRole().getId());
                    cleanRole.setName(u.getRole().getName());
                    clean.setRole(cleanRole);
                }

                // Не отправляем UserProfile (чтобы не было циклических ссылок)
                clean.setUserProfile(null);

                cleanUsers.add(clean);
            }

            System.out.println("✅ Загружено " + cleanUsers.size() + " пользователей для админа");

            return new Response(ResponseStatus.OK, "Список пользователей", gson.toJson(cleanUsers));

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка получения списка пользователей: " + e.getMessage(), null);
        }
    }

    // ==================== UPDATE_USER_ROLE ====================
    private Response handleUpdateUserRole(Request request) {
        try {
            if (currentUser == null || currentUser.getRole() == null ||
                    !"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
                return new Response(ResponseStatus.ERROR, "Только администратор может менять роли пользователей", null);
            }

            // Парсим JSON
            java.util.Map<String, Object> data = gson.fromJson(request.getRequestMessage(),
                    new com.google.gson.reflect.TypeToken<java.util.Map<String, Object>>(){}.getType());

            int userId = ((Double) data.get("userId")).intValue();
            String newRoleName = (String) data.get("role");

            if (newRoleName == null || newRoleName.trim().isEmpty()) {
                return new Response(ResponseStatus.ERROR, "Не указана новая роль", null);
            }

            userService.changeUserRole(userId, newRoleName.trim().toUpperCase());

            System.out.println("✅ Роль пользователя ID=" + userId + " изменена на " + newRoleName.toUpperCase() + " (админ: " + currentUser.getLogin() + ")");

            return new Response(ResponseStatus.OK, "Роль успешно изменена", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Ошибка смены роли: " + e.getMessage(), null);
        }
    }
}