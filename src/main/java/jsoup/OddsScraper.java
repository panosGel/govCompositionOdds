package jsoup;


import entities.Bet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OddsScraper {
    List<Bet> bets = new ArrayList<>();
    //TODO: scrape instead of hardcoding bet names
    List<String> betNames = Arrays.asList("Conservative Majority", "Conservative Minority",
            "Labour/Lib Dem Coalition", "Labour/Lib Dem/SNP Coalition", "Labour Majority", "Conservative/Lib Dem Coalition" );
    Document jsoupDoc;

    public OddsScraper(String url){
        try {
            jsoupDoc = Jsoup.connect(url)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        betNames.forEach(str -> {
            String query = "div.oddsTable:contains(" + str + ")" + " span";
            Elements odds = jsoupDoc.select(query);
            Bet bet = new Bet(str,odds.text());
            bets.add(bet);
        });
    }

    public List<Bet> getBets() {
        return bets;
    }
}
