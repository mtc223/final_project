import java.sql.*;

public class pm {
    private ui menu;

    public pm(ui a_menu){
        menu = a_menu;
    }

    //year = int_input(scan, conn.prepareStatement("select count(*) from takes where year=?"),"Year");
    public int pm_menu(Connection conn, int pid) throws SQLException{
        System.out.println("Property Management Menu. Property ID "+pid+". Select an option");
        System.out.println("(0) View apartments in this property");
        System.out.println("(1) View visit data");
        System.out.println("(2) Record visit data");
        System.out.println("(3) View lease data");
        System.out.println("(4) Record lease data");
        System.out.println("(5) Record move out");
        System.out.println("(6) Return to previous menu");
        int choice = menu.get_int_in_range(0,6);
        CallableStatement cstmt;
        boolean valid = false;
        int tid = 0;
        int aid = 0;
        int months = 0;
        switch(choice){
          case 0:
            menu.print_statement(conn, "select * from apartment where pid="+pid+" order by aid asc");
            break;
          case 1: 
            menu.print_statement(conn, "select tid,aid,tour from apartment natural join visit where pid="+pid+" order by aid asc");
            break;
          case 2: 
            System.out.println("Enter the prospective tenant ID");
            tid = menu.int_input(conn.prepareStatement("select count(*) from prospective where tid=?"),"TID");
            if(tid!=0){
              aid = menu.int_input(conn.prepareStatement("select count(*) from apartment where pid="+pid+" and aid=?"),"AID");
              if(aid!=0){
                valid = true;
              }
            }
            if(valid){
              cstmt = conn.prepareCall("{call recordvisit(?,?)}");
              cstmt.setInt(1,tid);
              cstmt.setInt(2,aid);
              menu.execute_csmt(cstmt, "Visit successfully recorded","This visit is already recorded.");
            }     
            break;
          case 3: 
            menu.print_statement(conn, "select tid,aid,deadline,balance,notice from apartment natural join lease where pid="+pid+" order by aid asc");
            break;
          case 4: 
            System.out.println("Enter the tenant ID");
            tid = menu.int_input(conn.prepareStatement("select count(*) from tenant where tid=?"),"TID");
            if(tid!=0){
              aid = menu.int_input(conn.prepareStatement("select count(*) from apartment where aid=? and aid not in (select aid from lease)"),"AID");
              if(aid!=0){
                System.out.println("Enter the length of the lease (months)");
                months = menu.get_int_in_range(1, 99999);
                valid = true;
              }
            }
            if(valid){
              cstmt = conn.prepareCall("{call recordlease(?,?,?)}");
              cstmt.setInt(1,tid);
              cstmt.setInt(2,aid);
              cstmt.setInt(3, months);
              menu.execute_csmt(cstmt, "Lease successfully created", "This lease already exists");
            }        
            break;
          case 5: 
            System.out.println("Enter the tenant ID");
            tid = menu.int_input(conn.prepareStatement("select count(*) from apartment natural join lease where pid="+pid+" and tid=?"),"TID");
            if(tid!=0){
              aid = menu.int_input(conn.prepareStatement("select count(*) from apartment natural join lease where pid="+pid+" and tid="+tid+" and aid=?"),"AID");
              if(aid!=0){
                valid = true;
              }
            }
            if(valid){
              cstmt = conn.prepareCall("{call recordmoveout(?,?)}");
              cstmt.setInt(1,tid);
              cstmt.setInt(2,aid);
              menu.execute_csmt(cstmt, "Lease successfully terminated", "Lease not successfully terminated");
            }  
            break;
          case 6: return -1;
        }
        return 0;
      }
}
