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
FROM film;
```
3. Получение N популярных фильмов:
```   
SELECT f.name,  
COUNT(l.user_id) AS rate
FROM film AS f
JOIN likes AS l ON f.film_id = l.film_id
GROUP BY  f.name
ORDER BY  rate DESC
LIMIT N;
```
4. Получение списка общих друзей:
```
SELECT u.name
FROM user."НЕОБХОДИМЫЙ ID".friends AS f
JOIN user."НЕОБХОДИМЫЙ ID 2".friends AS f2 ON f.user_id = f2.user_id
GROUP BY u.name;
```

# Пример SQL запроса:

1. Получение списка всех пользователей:
```
SELECT *
FROM users;
```
2. Получение списка всех фильмов:
```
SELECT *
FROM film;
```
3. Получение N популярных фильмов:
```   
SELECT f.name,  
COUNT(l.user_id) AS rate
FROM film AS f
JOIN likes AS l ON f.film_id = l.film_id
GROUP BY  f.name
ORDER BY  rate DESC
LIMIT N;
```
4. Получение списка общих друзей:
```
SELECT u.name
FROM user."НЕОБХОДИМЫЙ ID".friends AS f
JOIN user."НЕОБХОДИМЫЙ ID 2".friends AS f2 ON f.user_id = f2.user_id
GROUP BY u.name;
```