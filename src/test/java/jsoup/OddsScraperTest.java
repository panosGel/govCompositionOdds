package jsoup;

import entities.Bet;
import org.junit.Test;

import java.io.IOError;
import java.io.IOException;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;


public class OddsScraperTest {
    @Test
    public void scrapeOdds() throws Exception{
        String url = "http://www.paddypower.com/bet/politics/other-politics/uk-politics?ev_oc_grp_ids=282167";
        OddsScraper oddsScraper = new OddsScraper(url);
        List<Bet> bets = oddsScraper.getBets();
        assertThat(bets.size()).isEqualTo(6);
        bets.forEach(bet -> {
            System.out.println(bet.getTitle() + " " +  bet.getOdds());
        });
    }
}