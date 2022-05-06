import java.sql.*;

public class tenant {
    private ui menu;

    public tenant(ui a_menu){
        menu = a_menu;
    }

    public int tenant_menu(Connection conn, int tid) throws SQLException{
        System.out.println("Tenant Menu. Tenant ID "+tid+". Select an option");
        System.out.println("(0) Check payment status");
        System.out.println("(1) Make rental payment");
        System.out.println("(2) Add person");
        System.out.println("(3) Set move out date");
        System.out.println("(4) Update personal data");
        System.out.println("(5) Return to previous menu");
        int choice = menu.get_int_in_range(0,5);
        CallableStatement cstmt;
        boolean valid = false;
        int aid = 0;
        int tid2 = 0;
        int amount = 0;
        String date = "";
        switch(choice){
          case 0: 
            menu.print_statement(conn,"select tid,aid,balance from tenant natural join lease where tid="+tid+" order by aid asc");
            break;
          case 1: 
            menu.print_statement(conn,"select tid,aid,balance from tenant natural join lease where tid="+tid+" order by aid asc");
            System.out.println("Enter the apartment ID");
            aid = menu.int_input(conn.prepareStatement("select count(*) from lease where tid="+tid+" and aid=?"),"AID");
            if(aid!=0){
              System.out.println("Enter the payment amount");
              amount = menu.get_int_in_range(1, 99999);
              valid = true;
            }
            if(valid){
              cstmt = conn.prepareCall("{call makepayment(?,?,?)}");
              cstmt.setInt(1,tid);
              cstmt.setInt(2,aid);
              cstmt.setInt(3,amount);
              menu.execute_csmt(cstmt, "Payment successful", "Payment unsuccessful");
            }
            break;
          case 2: 
            menu.print_statement(conn,"select * from tenant order by tid asc");
            System.out.println("Enter the apartment ID");
            aid = menu.int_input(conn.prepareStatement("select count(*) from lease where tid="+tid+" and aid=?"),"AID");
            if(aid!=0){
              System.out.println("Enter the tenant ID to add to this lease");
              tid2 = menu.int_input(conn.prepareStatement("select count(*) from tenant where tid=?"),"TID");
              if(tid2==tid){
                System.out.println("That's your tenant ID");
                tid2 = 0;
              }
              if(tid2!=0){
                valid = true;
              }
            }
            if(valid){
              cstmt = conn.prepareCall("{call addperson(?,?,?)}");
              cstmt.setInt(1,tid);
              cstmt.setInt(2,aid);
              cstmt.setInt(3,tid2);
              menu.execute_csmt(cstmt, "Tenant added to lease","Tenant not added to lease");  
            }
            break;
          case 3: 
            menu.print_statement(conn,"select * from tenant natural join lease where tid="+tid+" order by aid asc");
            System.out.println("Enter the apartment ID");
            aid = menu.int_input(conn.prepareStatement("select count(*) from lease where tid="+tid+" and aid=?"),"AID");
            if(aid!=0){
              System.out.println("Enter the move-out date (yyyy-mm-dd)");
              date = menu.get_date();
              valid = true;
            }
            if(valid){
              cstmt = conn.prepareCall("{call updatemoveout(?,?,?)}");
              cstmt.setInt(1,tid);
              cstmt.setInt(2,aid);
              cstmt.setString(3,date);
              menu.execute_csmt(cstmt, "Move out date updated","Move out date not updated");
            }
             break;
          case 4: 
            menu.print_statement(conn,"select * from tenant where tid="+tid);
            System.out.println("Type the precise characteristic to update");
            String[] characteristics = {"name","phone","prior_add","ssn","bank"};
            String input = "";
            input = menu.get_string();
            for(String characteristic: characteristics){
              if(input.toUpperCase().equals(characteristic.toUpperCase())){
                valid = true;
              }
            }
            if(valid){
              System.out.println("What are you changing "+input+" to?");
              PreparedStatement pstmt = conn.prepareStatement("update tenant set "+input+"=? where tid="+tid);
              switch(input.toLowerCase()){
                case "name":
                  String name = menu.get_string();
                  if(!name.toLowerCase().equals("name")){
                    pstmt.setString(1, name);
                    pstmt.executeQuery();
                  }
                  else{
                    System.out.println("What are you trying to do?");
                  }
                  break;
                case "phone":
                  int phone = menu.get_int_in_range(0,999999999);
                  pstmt.setInt(1, phone);
                  pstmt.executeQuery();
                  break;
                case "prior_add":
                  String prior_add = menu.get_string();
                  if(!prior_add.toLowerCase().equals("prior_add")){
                    pstmt.setString(1, prior_add);
                    pstmt.executeQuery();
                  }
                  else{
                    System.out.println("What are you trying to do?");
                  }
                  break;
                case "ssn":
                  int ssn = menu.get_int_in_range(0,999999999);
                  pstmt.setInt(1, ssn);
                  pstmt.executeQuery();
                  break;
                case "bank":
                  String bank = menu.get_string();
                  if(!bank.toLowerCase().equals("bank")){
                    pstmt.setString(1, bank);
                    pstmt.executeQuery();
                  }
                  else{
                    System.out.println("What are you trying to do?");
                  }
                  break;
              }
              menu.print_statement(conn, "select * from tenant where tid="+tid);
            }
            break;
          case 5: return -1;
        }
        return 1;
      }

      public int pros_menu(Connection conn, int tid) throws SQLException{
        System.out.println("Prospective Tenant Menu. Select an option");
        System.out.println("(0) See available apartments");
        System.out.println("(1) Upgrade to tenant");
        System.out.println("(2) Return to previous menu");
        int choice = menu.get_int_in_range(0,2);
        CallableStatement cstmt;
        switch(choice){
          case 0: menu.print_statement(conn,"select * from apartment where aid not in (select aid from lease) order by aid asc");
            break;
          case 1: 
            System.out.println("Enter the prior address");
            String prior_add = menu.get_string();
            System.out.println("Enter social security number");
            int ssn = menu.get_int_in_range(0, 999999999);
            System.out.println("Enter bank information");
            String bank = menu.get_string();
            cstmt = conn.prepareCall("{call upgradepros(?,?,?,?)}");
            cstmt.setInt(1,tid);
            cstmt.setString(2,prior_add);
            cstmt.setInt(3,ssn);
            cstmt.setString(4, bank);
            menu.execute_csmt(cstmt, "New tenant added", "New tenant already exists");
            break;
          case 2: return -1; 
        }
        return 1;
      }
}
