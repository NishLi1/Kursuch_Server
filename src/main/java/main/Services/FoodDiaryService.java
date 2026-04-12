package main.Services;

import main.DataAccessObjects.FoodDiaryDAO;
import main.DataAccessObjects.FoodEntryDAO;
import main.Models.Entities.FoodDiary;
import main.Models.Entities.FoodEntry;
import main.Models.Entities.User;

import java.time.LocalDate;
import java.util.List;

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
     * Добавить запись о приёме пищи в дневник
     */
    public void addFoodEntry(int userId, LocalDate date, FoodEntry entry) {
        FoodDiary diary = getOrCreateDiary(userId, date);

        entry.setDiary(diary);        // важная связь
        entryDAO.save(entry);
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