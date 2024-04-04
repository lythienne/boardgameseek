public class SimilarityScore implements Comparable
{
   double[] scores;
   double total;
   String game;
   String otherGame;

   private int index;

   public SimilarityScore(String game1, String game2)
   {
      game = game1;
      otherGame = game2;

      index = 0;
      scores = new double[9];
   }

   public void addScore(double score)
   {
      scores[index] = score;
      total += score;
      index++;
   }

   @Override
   public int compareTo(Object other)
   {
      if(!(other instanceof SimilarityScore)) 
         throw new IllegalArgumentException("Other not Similarity Score");
      else if(((SimilarityScore) other).game != this.game)
         throw new IllegalArgumentException("Not comparing Similarity scores of the same game");

      return ((Double) total).compareTo(((SimilarityScore) other).total);
   }
}
