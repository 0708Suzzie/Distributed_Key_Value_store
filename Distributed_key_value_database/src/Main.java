import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void subMenu(Node node) {
        int flag = 1;
        while(flag ==1 ) {

            System.out.println("Enter your Choice: ");
            System.out.println("1) Set key ");
            System.out.println("2) Get Key ");
            System.out.println("3) Delete Key ");
            System.out.println("4) Get All");
            System.out.println("5) Exit");

            Scanner sc = new Scanner(System.in);
            int userInput = sc.nextInt();
            switch (userInput) {
                case 1: {
                    node.setKey();
                    break;
                }
                case 2: {
                    node.getKey();
                    break;
                }

                case 3: {
                    node.deleteKey();
                    break;
                }

                case 4: {
                    node.getAll();
                    break;
                }

                case 5: {
                    flag = 0;
                    break;

                }
                default: {
                    System.out.println("Unacceptable input");
                    break;
                }


            }
        }

    }

    public static void main(String[] args) {
        try {
            Node node = new Node();

            int flag = 1;
            while(flag == 1){
                System.out.println("Enter your Choice: ");
                System.out.println("1) Create Database ");
                System.out.println("2) Get Database ");
                System.out.println("3) Drop Database ");
                System.out.println("4) Exit");

                Scanner sc = new Scanner(System.in);
                int userInput = sc.nextInt();
                switch (userInput){
                    case 1:{
                        node.createDatabase();
                        subMenu(node);
                        break;
                    }
                    case 2:{
                        node.getDatabase();
                        subMenu(node);
                        break;
                    }
                    case 3:{
                        node.dropDatabase();
                        break;
                    }
                    case 4:{
                        node.disableNode();
                        flag = 0;
                        break;
                    }
                    default:{
                        System.out.println("Unacceptable input");
                        break;
                    }

                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }



}