package chess.intel.technique.util;

public class TechniqueLogic {
    
    public static int timeTo(int kingFile, int kingRank, int destinationFile, int destinationRank, boolean initialMove) {
        return Math.max(2 * Math.max(Math.abs(destinationFile - kingFile), Math.abs(destinationRank - kingRank)) - (initialMove ? 1 : 0), 0);
    }
}
