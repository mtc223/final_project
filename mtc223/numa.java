import java.sql.*;

public class numa {
    private ui menu;

    public numa(ui a_menu){
        menu = a_menu;
    }

    public int numa_menu(Connection conn) throws SQLException{
        System.out.println("NUMA Manager Menu. Select an option");
        System.out.println("(0) Add a new property");
        System.out.println("(1) Add apartments");
        System.out.println("(2) View all apartments and properties");
        System.out.println("(3) Add prospective tenant");
        System.out.println("(4) Return to previous menu");
        int choice = menu.get_int_in_range(0,4);
        CallableStatement cstmt;
        boolean valid = true;
        switch(choice){
          case 0: 
            System.out.println("Enter the address");
            String address = menu.get_string();
            System.out.println("Enter the name of the property");
            String name = menu.get_string();
            cstmt = conn.prepareCall("{call createproperty(?,?)}");
            cstmt.setString(1,address);
            cstmt.setString(2,name);
            menu.execute_csmt(cstmt, "New property added", "Property not created");
            break;
          case 1: 
            System.out.println("Enter property to add apartments to");
            int pid = menu.int_input(conn.prepareStatement("select count(*) from property where pid=?"),"PID");
            if(pid==0){
              valid = false;
            }
            if(valid){
              System.out.println("Enter the number of apartments to build");
              int count = menu.get_int_in_range(0,100);
              cstmt = conn.prepareCall("{call generateapartments(?,?)}");
              cstmt.setInt(1,pid);
              cstmt.setInt(2,count);
              menu.execute_csmt(cstmt, "New apartments created", "Apartments not created");
            }
            break;
          case 2:
            menu.print_statement(conn, "select * from property natural join apartment");
            break;
          case 3:
            System.out.println("Enter the name");
            String aname = menu.get_string();
            System.out.println("Enter the phone number");
            long phone = menu.getphone();
            cstmt = conn.prepareCall("{call addpros(?,?)}");
            cstmt.setString(1,aname);
            cstmt.setLong(2,phone);
            menu.execute_csmt(cstmt, "New prospective tenant added", "New prospective tenant not added");
            break;
          case 4: return -1;
        }
        return 2;
      }
}
