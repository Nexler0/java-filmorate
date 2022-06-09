package ru.yandex.practicum.filmorate.model;

public enum Rate {
    G("Нет возрастных ограничений"),
    PG("Детям с родителями"),
    PG_13("Детям до 13 просмотр не жедателен"),
    R("Лицам до 17, только в присутствии взрослых"),
    NC_17("Лицам до 18 просмотр запрещен");

    private final String rate;

    Rate(String rate){
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }
}
