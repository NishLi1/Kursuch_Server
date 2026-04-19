package main.Services;

import main.DataAccessObjects.FoodDiaryDAO;
import main.DataAccessObjects.FoodEntryDAO;
import main.Models.Entities.*;


import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class FoodDiaryService {

    private final FoodDiaryDAO diaryDAO = new FoodDiaryDAO();
    private final FoodEntryDAO entryDAO = new FoodEntryDAO();

    /**
     * Получить дневник пользователя за дату.
     * Если дневника ещё нет — создаёт новый.
     */
    public FoodDiary getOrCreateDiary(int userId, LocalDate date) {
        FoodDiary diary = diaryDAO.findByUserAndDate(userId, date);

        if (diary == null) {
            diary = new FoodDiary();
            diary.setUser(new User());           // создаём ссылку на пользователя
            diary.getUser().setId(userId);
            diary.setDate(date);
            diaryDAO.save(diary);
        }
        return diary;
    }
    /**
     * Добавляет запись о приёме пищи в дневник пользователя
     * @return true, если запись успешно сохранена
     */
    public boolean addFoodEntry(int userId, LocalDate date, FoodEntry entry) {
        try {
            if (entry == null || entry.getProduct() == null || entry.getWeight() == null || entry.getWeight() <= 0) {
                System.err.println("❌ Некорректные данные");
                return false;
            }

            FoodDiary diary = diaryDAO.getOrCreateDiary(userId, date);
            if (diary == null) {
                System.err.println("❌ Не удалось создать дневник");
                return false;
            }

            entry.setDiary(diary);

            // === РАСЧЁТ КБЖУ ===
            Product product = entry.getProduct();
            double weight = entry.getWeight();
            double factor = weight / 100.0;

            entry.setCalories(product.getCalories() * factor);
            entry.setProteins(product.getProteins() * factor);
            entry.setFats(product.getFats() * factor);
            entry.setCarbs(product.getCarbs() * factor);

            // Сохраняем
            entryDAO.save(entry);

            System.out.println("✅ Запись добавлена | " + product.getName() +
                    " | Вес: " + weight + "г | Ккал: " + entry.getCalories());

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Возвращает все записи дневника пользователя за указанную дату
     */
    public List<FoodEntry> getDiaryEntries(int userId, LocalDate date) {
        try {
            FoodDiary diary = diaryDAO.getOrCreateDiary(userId, date);

            if (diary == null) {
                return new ArrayList<>();
            }

            return entryDAO.findByDiaryId(diary.getId());

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    /**
     * Получить все записи за день
     */
    public List<FoodEntry> getEntriesForDate(int userId, LocalDate date) {
        FoodDiary diary = getOrCreateDiary(userId, date);
        return entryDAO.findByDiaryId(diary.getId());
    }

    /**
     * Удалить запись из дневника
     */
    public void deleteFoodEntry(int entryId) {
        FoodEntry entry = entryDAO.findById(entryId);
        if (entry != null) {
            entryDAO.delete(entry);
        }
    }

    /**
     * Получить дневник по ID
     */
    public FoodDiary getDiaryById(int diaryId) {
        return diaryDAO.findById(diaryId);
    }
}