import java.io.*;
import java.util.*;

public class DataCleaner
{
   /* 
    0. name
    1. year
    2. summary
    3. playerCount
    4. time
    5. age
    6. altNames
    7. designers
    8. artists
    9. publishers
    10. categories
    11. mechanisms
    12. description
    */
   private BufferedReader dataStream;
   private String currentLine;
   private ArrayList<ArrayList<String>[]> dataTable;
   private HashMap<String, Integer> gameIndexes;

   public DataCleaner() throws FileNotFoundException, IOException
   {
      File file = new File("/Users/harrisonchen/Documents/python/gameRec/gameData.txt");
      dataStream = new BufferedReader(
         new InputStreamReader(new FileInputStream(file)));
      currentLine = dataStream.readLine();

      dataTable = new ArrayList<ArrayList<String>[]>();
      gameIndexes = new HashMap<String, Integer>();
   }

   public ArrayList<ArrayList<String>[]> getDataTable() throws IOException
   {
      putDataInTable();
      return dataTable;
   }

   public HashMap<String, Integer> getIndexes()
   {
      return gameIndexes;
   }

   private void putDataInTable() throws IOException
   {
      int gameIndex = 0;
      while(currentLine != null)
      {
         ArrayList<String>[] gameData = new ArrayList[12];
         String dataString = currentLine;
         String name = null;

         int separatorIndex = dataString.indexOf("|");
         for(int i = 0; i < 13; i++)
         {
            String dataCategory = dataString.substring(0, separatorIndex);
            dataString = dataString.substring(separatorIndex+1);
            separatorIndex = dataString.indexOf("|");

            int index = (i < 6)? i : i-1;

            switch (i) 
            {
               case 0:
                  name = dataCategory;
                  gameData[index] = new ArrayList<String>();
                  gameData[index].add(dataCategory);
                  break;
               case 1,2,12: //list is one string
                  gameData[index] = new ArrayList<String>();
                  gameData[index].add(clean(dataCategory));
                  break;
               case 3,4,5: //list is 1 or 2 strings
                  int plus = dataCategory.indexOf("+");
                  if(plus > -1)
                  {
                     gameData[index] = new ArrayList<String>(2);
                     gameData[index].add(clean(dataCategory.substring(0, plus)));
                     gameData[index].add("100");
                  }
                  else
                     gameData[index] = getSeparatedList(dataCategory, "–");
                  break;
               case 7,8,9,10,11:
                  gameData[index] = getSeparatedList(dataCategory, ",");
                  break;
               case 6: /*don't want altNames*/ break;
               default:
                  break;
            }
         }
         /* if didn't scrape properly or game has a repeated name, don't add */
         if(name!=null && !name.equals("") && gameIndexes.get(name) == null)
         {
            dataTable.add(gameData);
            gameIndexes.put(name, gameIndex);
            gameIndex++;
         }
         currentLine = dataStream.readLine();
      }
   }

   private ArrayList<String> getSeparatedList(String list, String separator)
   {
      ArrayList<String> stringList = new ArrayList<String>();
      int index = list.indexOf(separator);
      if(index < 0)
         stringList.add(list);
      while(index > 0)
      {
         stringList.add(clean(list.substring(0, index)));
         list = list.substring(index+1);
         index = list.indexOf(separator);
      }
      return stringList;
   }

   public String clean(String dirtyString)
   {
      String cleanString = dirtyString.toLowerCase();
      return cleanString.replaceAll("\\s","");
   }
}
