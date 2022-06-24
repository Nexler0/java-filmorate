package ru.yandex.practicum.filmorate.model;

public enum Rate {
    G("G"),
    PG("PG"),
    PG_13("PG_13"),
    R("R"),
    NC_17("NC_17");

    private final String rate;

    Rate(String rate) {
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }

    public String getRateIndex() {
        switch (rate) {
            case "G":
                return "1";
            case "PG":
                return "2";
            case "PG_13":
                return "3";
            case "R":
                return "4";
            case "NC_17":
                return "5";
            default:
                return null;
        }
    }

    public String getRateDescription() {
        switch (rate) {
            case "G":
                return "Нет возрастных ограничений";
            case "PG":
                return "Детям с родителями";
            case "PG_13":
                return "Детям до 13 просмотр не жедателен";
            case "R":
                return "Лицам до 17, только в присутствии взрослых";
            case "NC_17":
                return "Лицам до 18 просмотр запрещен";
            default:
                return null;
        }
    }
}