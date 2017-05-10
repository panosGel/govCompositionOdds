package entities;

import io.vertx.core.json.JsonObject;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class BetTest {
    private final Bet bet = new Bet("bet1","20/1");
    private final JsonObject jsonObject = new JsonObject()
            .put("title", "bet1")
            .put("odds", "20/1");

    @Test
    public void empty_bet() throws Exception {
        Bet bet = new Bet();
        assertThat(bet.getId()).isEqualTo("");
        assertThat(bet.getOdds()).isNull();
        assertThat(bet.getTitle()).isNull();
    }

    @Test
    public void bet() throws Exception {
        assertThat(bet.getTitle()).isEqualTo("bet1");
        assertThat(bet.getOdds()).isEqualTo("20/1");
    }

    @Test
    public void to_json() throws Exception {

        assertThat(bet.toJson()).isEqualTo((jsonObject));
    }

    @Test
    public void from_json() throws Exception {
        assertThat(new Bet(jsonObject)).isEqualTo(bet);
    }
}