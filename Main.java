import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class Main
{
   public static void main(String[] args) throws FileNotFoundException, IOException
   {
      DataCleaner dataCleaner = new DataCleaner();
      ArrayList<ArrayList<String>[]> dataTable = dataCleaner.getDataTable();
      HashMap<String, Integer> indexes = dataCleaner.getIndexes();
      DataAnalyzer dataAnalyzer = new DataAnalyzer(dataTable, indexes);
      dataAnalyzer.compareEntries();

      Scanner in = new Scanner(System.in);
      System.out.println("Enter the name of a game:");
      String game = in.nextLine();
      ArrayList<String> games = new ArrayList<>();
      games.add("Dominion");
      games.add("Hero Realms");
      games.add("Star Realms");

      while(!game.equals("q"))
      {
         SimilarityScore[] simularGames = dataAnalyzer.mostSimilarGames(game, 5);
         if(simularGames == null)
            System.out.println(game + " is not in the game database");
         else
         {
            System.out.println();

            for(SimilarityScore simularGame : simularGames)
            {   
               System.out.println(simularGame.otherGame);
               for(double score : simularGame.scores)
                  System.out.print(score + ",");
               System.out.println("\nTotal Similarity: "+simularGame.total+"\n");
            }
         }   
         System.out.println("--------------------------");
         System.out.println("Enter the name of a game:");
         game = in.nextLine();
      }
   }
}