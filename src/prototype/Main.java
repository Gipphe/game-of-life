package prototype;

public class Main {
    public static void main(String[] args){
        Grid grid = new Grid();

        try {
            while(true) {
                grid.tick();
                System.out.println(grid);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(grid);
    }
}
