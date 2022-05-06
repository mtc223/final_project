import java.sql.*;

public class bm {
    private ui menu;

    public bm(ui a_menu){
        menu = a_menu;
    }

    public int business_menu(Connection conn) throws SQLException{
        System.out.println("Business Manager Menu");
        System.out.println("(0) View all properties and apartments");
        System.out.println("(1) View all tenants");
        System.out.println("(2) View special apartments");
        System.out.println("(3) View features and amenities");
        System.out.println("(4) Return to previous menu");
        int choice = menu.get_int_in_range(0,4);
        switch(choice){
          case 0: 
            menu.print_statement(conn, "select * from property natural join apartment");
            break;
          case 1:
            menu.print_statement(conn, "select * from (select tid,name,phone from tenant UNION select * from prospective)");
            break;
          case 2:
            System.out.println("Underwater apartments");
            menu.print_statement(conn, "select * from underwater");
            System.out.println("Triangular apartments");
            menu.print_statement(conn, "select * from triangular");
            System.out.println("Windowless apartments");
            menu.print_statement(conn, "select * from windowless");
            break;
          case 3:
            System.out.println("Amenities");
            menu.print_statement(conn, "select * from property natural join amenities");
            System.out.println("Features");
            menu.print_statement(conn, "select * from apartment natural join features");
            break;
          case 4: return -1;
        }
        return 3;
      }
}