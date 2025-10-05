package dev.rnandor.paisho;

public class Main {
    public static void main(String[] args) {
        for(int i = 8; i >= -8; i--) {
            for (int j = -8; j <= 8; j++) {
                int dist = (int) Math.sqrt(i * i + j * j);
                if(dist <= 8)
                    System.out.print("+ ");
                else
                    System.out.print("0 ");
            }
            System.out.println();
        }
        System.out.println();
        Table t = new Table();

        Integer mask = t.getType(-8, -0).get();
        System.out.println("Tem: "+Table.isTemple(mask));
        System.out.println("Red: "+Table.isRedGarden(mask));
        System.out.println("Whi: "+Table.isWhiteGarden(mask));
    }
}