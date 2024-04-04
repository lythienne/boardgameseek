import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class DataAnalyzer 
{
   /* 
    0. name
    1. year
    2. summary
    3. playerCount
    4. time
    5. age
    7. designers
    8. artists
    9. publishers
    10. categories
    11. mechanisms
    12. description
    */
   private ArrayList<ArrayList<String>[]> dataTable;
   private HashMap<String, Integer> indexMap;
   private int entries;
   private SimilarityScore[][] scoreTable;
   private double[] weights = {0,2,0,7,10,1,5,2,3,7,15,0};
   
   public DataAnalyzer(ArrayList<ArrayList<String>[]> table, HashMap<String, Integer> indexes)
   {
      dataTable = table;
      indexMap = indexes;
      entries = dataTable.size();
      scoreTable = new SimilarityScore[entries][entries];
   }

   public SimilarityScore[][] getScores()
   {
      compareEntries();
      return scoreTable;
   }

   public SimilarityScore[] mostSimilarGames(ArrayList<String> gameNames, int numGames)
   {
      ArrayList<SimilarityScore> simScores = new ArrayList<>();

      HashSet<String>[] games = new HashSet[12];
      for(int i = 0; i < 12; i++)
      {
         games[i] = new HashSet<String>();
         for(String name : gameNames)
         {
            int index = indexMap.get(name);
            ArrayList<String> attributes =  dataTable.get(index)[i];
            for(String attribute : attributes)
               games[i].add(attribute);
         }
      }

      boolean testGame = true;
      for(ArrayList<String>[] otherGame : dataTable)
      {
         String otherName = otherGame[0].get(0);
         for (String name : gameNames) 
         {
            if(otherName.equals(name))
               testGame = false;
         }
         if(!testGame) continue;

         SimilarityScore gameScores = new SimilarityScore("Multiple Games", otherName);

         for(int i = 0; i < 12; i++)
         {
            HashSet<String> category = games[i];
            ArrayList<String> otherCategory = otherGame[i];

            double score = calculateScore(i, otherCategory, category);  
            //swap because want to know how many things from other game are similar to this game, so know how easy to learn

            if(score > -1)
               gameScores.addScore(score * weights[i]);
         } //for(int i = 0; i < 12; i++)
         
         simScores.add(gameScores);
      }

      SimilarityScore[] closestGames = new SimilarityScore[numGames];
      
      PriorityQueue<SimilarityScore> pq = new PriorityQueue<>();

      for(int i = 0; i < simScores.size(); i++) //i is game compared to
      {
         if(simScores.get(i) == null)
            System.out.println("hi");
         pq.add(simScores.get(i));
         if(pq.size() > numGames) 
            pq.remove();
      }

      for(int i = 0; i < numGames; i++) 
         closestGames[i] = pq.poll();

      return closestGames;
   }

   public SimilarityScore[] mostSimilarGames(String gameName, int numGames)
   {
      Integer index = indexMap.get(gameName);

      if(index == null)
         return null;

      SimilarityScore[] closestGames = new SimilarityScore[numGames];
      
      PriorityQueue<SimilarityScore> pq = new PriorityQueue<>();

      for(int i = 0; i < scoreTable[index].length; i++) //i is game compared to
      {
         if(i == index) continue;
         if(scoreTable[index][i] == null)
            System.out.println("hi");
         pq.add(scoreTable[index][i]);
         if(pq.size() > numGames) 
            pq.remove();
      }

      for(int i = 0; i < numGames; i++) 
         closestGames[i] = pq.poll();

      return closestGames;
   }

   public void compareEntries()
   {
      fixWeights();
      for(ArrayList<String>[] game1 : dataTable) 
      {
         for(ArrayList<String>[] game2 : dataTable) 
         {
            String name = game1[0].get(0);
            String otherName = game2[0].get(0);

            int index = indexMap.get(name);
            int otherIndex = indexMap.get(otherName);

            if(name.equals(otherName))
            {
               scoreTable[index][otherIndex] = null; 
               continue;
            }

            SimilarityScore gameScores = new SimilarityScore(name, otherName);

            for(int i = 0; i < 12; i++)
            {
               ArrayList<String> category = game1[i];
               ArrayList<String> otherCategory = game2[i];

               double score = calculateScore(i, otherCategory, category);  
               //swap because want to know how many things from other game are similar to this game, so know how easy to learn

               if(score > -1)
                  gameScores.addScore(score * weights[i]);
            } //for(int i = 0; i < 12; i++)
            scoreTable[index][otherIndex] = gameScores;
         } //for(ArrayList<String>[] game2 : dataTable) 
      } //for(ArrayList<String>[] game1 : dataTable) 
   }

   public void saveTable(String tableName)
   {
      try
      {
         File file = new File(tableName);
         DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

         for (int i = 0; i < scoreTable.length; i++)
            for (int j = 0; j < scoreTable[i].length; j++)
               for (int k = 0; k < scoreTable[i][j].scores.length; k++)
                  out.writeDouble(scoreTable[i][j].scores[k]);
         
         out.close();
      } // try
      catch (Exception e)
      {
         System.exit(-1);
      } // try catch (Exception e)
   }

   private void fixWeights()
   {
      double total = 0.0;
      for (double weight : weights)
         total+=weight;
      for (int i = 0; i < weights.length; i++)
         weights[i] /= total;
   }

   private double calculateScore(int index, Iterable<String> category1, Iterable<String> category2)
   {
      switch (index) 
      {
         case 0,2,11: //don't compare
            return -1;
         /* 
    0. name

    1. year
    2. playerCount
    3. time
    4. age
    5. designers
    6. artists
    7. publishers
    8. categories
    9. mechanisms 
    
    1. A, B, C = 2/3
    2. B, C, D, E, F = 2/5 <- should be game 1

    11. description
    */
         case 1,6,7,8,9,10: //
            double shared = 0;
            int total = 0;
            for(String item1 : category1)
            {
               total++;
               for(String item2 : category2)
               {
                  if(item1.equals("") || item2.equals("") 
                        || item1.equals("N/A") || item2.equals("N/A"))
                     return 0.0;
                  else if(item1.equals(item2)) shared++;
               }
            } 
            return shared / total;
         case 3,4,5: //numbers are range
            List<Integer> range1 = new ArrayList<>();
            List<Integer> range2 = new ArrayList<>();
            for(String number : category1)
               range1.add(Integer.parseInt(number));
            for(String number : category2)
               range2.add(Integer.parseInt(number));
            if(range1.size() == 1)
               range1.add(range1.get(0));
            if(range2.size() == 1)
               range2.add(range2.get(0));
            return (intersects(range1.get(0), range1.get(1), range2.get(0), range2.get(1)))? 1 : 0; //score = 1 if intersect, 0 if not
         default:
            return -1;
      }
   }

   private boolean intersects(int start1, int stop1, int start2, int stop2)
   {
      return start2 >= start1 && start2 <= stop1 || stop2 >= start1 && stop2 <= stop1;
   }
}
