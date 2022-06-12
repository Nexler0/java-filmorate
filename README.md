# **Filmorate**

Зачаток приложения для хранения фильмов с частитцей социальной сети. 

Приложение умеет:
1. Хранить список фильмов;
2. Хранить список прользователей;
3. Добавлять, выводить и удалять фильмы, пользователей;
4. Хранить примитивную социальную связь между пользователями;

# **ER-DIAGRAM**
<picture>
  <img alt="Схема Базы Данных" src="src\BD_scheme.png">
</picture>

# Пример SQL запроса:

1. Получение списка всех пользователей:
```
SELECT *
FROM users;
```
2. Получение списка всех фильмов:
```
SELECT *
FROM films;
```
3. Получение N популярных фильмов:
```   
SELECT f.name,  
COUNT(l.user_id) AS rate
FROM films AS f
JOIN likes AS l ON f.film_id = l.film_id
GROUP BY  f.name
ORDER BY  rate DESC
LIMIT N;
```
4. Получение списка общих друзей:
```
SELECT u.name
FROM friends."НЕОБХОДИМЫЙ ID" AS f1
JOIN friends."ДРУГОЙ ID" AS f2 ON f.friend_id = f2.friend_id
JOIN users AS u ON f.friend_id = u.user_id
GROUP BY u.name;
```