package byow.Core;
import java.util.*;
import byow.Core.World;
import byow.TileEngine.TETile;

public class trial {

    static class Bode
    {
        // (x, y) represents coordinates of a cell in the matrix
        int x, y;

        // maintain a parent node for printing the final path
        Bode parent;

        Bode(int x, int y, Bode parent)
        {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return x+"|"+y;
        }
    }
    public static class Thing {
        // Below arrays detail all four possible movements from a cell
        private static int[] row = { -1, 0, 0, 1 };
        private static int[] col = { 0, -1, 1, 0 };

        // The function returns false if (x, y) is not a valid position
        private static boolean isValid(int x, int y) {
            return (x >= 0 && x < 60) && (y >= 0 && y < 30);
        }

        // Utility function to find path from source to destination
        private static void findPath(Bode bode, List<String> path)
        {
            if (bode != null) {
                findPath(bode.parent, path);
                path.add(bode.toString());
            }
        }

        // Find the shortest route in a matrix from source cell (x, y) to
        // destination cell (N-1, N-1)
        public static List<String> findPath(TETile[][] matrix, int x, int y, World.Position targetRoom)
        {
            // list to store shortest path
            List<String> path = new ArrayList<>();

            // base case
            if (matrix == null || matrix.length == 0) {
                System.out.println(path);
                return path;
            }


            // create a queue and enqueue the first node
            Queue<Bode> q = new ArrayDeque<>();
            Bode src = new Bode(x, y, null);
            q.add(src);

            // set to check if the matrix cell is visited before or not
            Set<String> visited = new HashSet<>();

            String key = src.x + "," + src.y;
            visited.add(key);

            // loop till queue is empty
            while (!q.isEmpty())
            {
                // dequeue front node and process it
                Bode curr = q.poll();
                int i = curr.x, j = curr.y;

                // return if the destination is found
                if (targetRoom.x == i && targetRoom.y == j) {
                    findPath(curr, path);
                    System.out.println(path);
                    return path;
                }

                // value of the current cell

                // check all four possible movements from the current cell
                // and recur for each valid movement
                for (int k = 0; k < row.length; k++)
                {
                    // get next position coordinates using the value of the current cell
                    x = i + row[k];
                    y = j + col[k];

                    // check if it is possible to go to the next position
                    // from the current position
                    if (isValid(x, y))
                    {
                        // construct the next cell node
                        Bode next = new Bode(x, y, curr);

                        key = next.x + "," + next.y;

                        // if it isn't visited yet
                        if (!visited.contains(key))
                        {
                            // enqueue it and mark it as visited
                            q.add(next);
                            visited.add(key);
                        }
                    }
                }
            }

            // we reach here if the path is not possible
            System.out.println(path);
            return path;
        }

        public static void main(String[] args) {
            List<String> thing = new ArrayList<>();
            thing.add("(1, 2)");
            System.out.println(thing);
            String newthing = thing.get(0);
            System.out.println(Integer.parseInt(String.valueOf(newthing.charAt(1))));
            System.out.println(Integer.parseInt(String.valueOf(newthing.charAt(4))));
        }
    }
}
