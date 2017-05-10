package entities;

import io.vertx.core.json.JsonObject;

public class Bet {

    private String id;
    private String title;
    private String odds;



    public Bet(String title, String odds) {
        this.id = "";
        this.title = title;
        this.odds = odds;
    }

    public Bet() { this.id = ""; }

    public Bet(JsonObject entry) {
        this.title = entry.getString("title");
        this.odds = entry.getString("odds");
        this.id = entry.getString("_id");
    }

    public String getTitle() { return title; }
    public String getOdds() { return odds; }

    public String getId() { return id; }

    public void setTitle(String title) { this.title = title; }
    public void setOdds(String odds) { this.odds = odds; }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject()
                .put("title", title)
                .put("odds", odds);
        if( id != null && !id.isEmpty()) {
            jsonObject.put("_id", id);
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "Bet{" +
                "title='" + title + '\'' +

                ", odds='" + odds + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bet bet = (Bet) o;

        if (title != null ? !title.equals(bet.title) : bet.title != null) return false;
        return odds != null ? odds.equals(bet.odds) : bet.odds == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (odds != null ? odds.hashCode() : 0);
        return result;
    }

}
