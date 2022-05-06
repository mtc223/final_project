import java.util.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ui {
    private String user_id;
    private String passwd;
    private Scanner scan;
    private pm prop_man;
    private bm bus_man;
    private numa numa_man;
    private tenant ten;

    public ui(){
      scan = new Scanner(System.in);
    }

    public long getphone(){
      long input = -1;
      while(input==-1){
        if(scan.hasNextLong()){
          input = scan.nextLong();
          if(input>10000000000L || input<999999999){
            System.out.println("Phone number not in range");
            input = -1;
          }
        }
        else{
          System.out.println("Not a phone number");
        }
        scan.nextLine();
      }
      return input;
    }

    public int get_int_in_range(int low, int high){
      int input = -1;
      while(input == -1){
        if(scan.hasNextInt()){
          input = scan.nextInt();
          if(input < low || input > high){
            System.out.println("Int not in range. Must be between " + low + " and " + high);
            input = -1;
          }
        }
        else{
          System.out.println("Not an int");
        }
        scan.nextLine();
      }
      return input;
    }

    public String get_date(){
      String input = "";
      while(input.equals("")){
        if(scan.hasNext()){
          input = scan.nextLine();
          try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.parse(input);
          }catch(ParseException p){
            System.out.println("Invalid date format. Must be yyyy-mm-dd");
            input = "";
          }
        }
        else{
          System.out.println("Not a date");
        }
        scan.nextLine();
      }
      return input;
    }

    public String get_string(){
      String input = "";
      while(input.equals("")){
        if(scan.hasNextLine()){
          input = scan.nextLine();
        }
        else{
          System.out.println("Not a string");
        }
      }
      return input;
    }

    /** 
    * This method checks for int inputs from the scanner and verifies
    * they exist in the database with the prepared statement. 
    * @param scan Scanner for system.in
    * @param pstmt Prepared statement for the database including ?
    * @param label A label to prompt the user
    * @return the valid input
    */
    public int int_input(PreparedStatement pstmt, String label) throws SQLException{
      int input = 0;
      try{
        System.out.println("Enter "+label+": ");
        input = get_int_in_range(0, 99999);
      }catch(InputMismatchException e){
        System.out.println("Int expected, try again.");
        return 0;
      }
      if(input==0)System.exit(0);
      pstmt.setInt(1,input);
      ResultSet rset = pstmt.executeQuery();
      if(rset.next()){
        if(rset.getString("count(*)").equals("0")){
          System.out.println(label+" "+input+" not found in database");
          return 0;
        }
      }
      return input;
    }

    /** 
    * This method checks for string inputs from the scanner and verifies
    * they exist in the database with the prepared statement. 
    * @param scan Scanner for system.in
    * @param pstmt Prepared statement for the database including ?
    * @param label A label to prompt the user
    * @return the valid input
    */
    public String str_input(PreparedStatement pstmt, String label) throws SQLException{
      String input = "";
      try{
        System.out.println("Enter "+label+": ");
        input = scan.nextLine();
      }catch(InputMismatchException e){
        System.out.println("String expected, try again.");
        return "";
      }
      if(input.equals("0"))System.exit(0);
      pstmt.setString(1,input);
      ResultSet rset = pstmt.executeQuery();
      if(rset.next()){
        if(rset.getString("count(*)").equals("0")){
          System.out.println(label+" "+input+" not found in database");
          return "";
        }
      }
      return input;
    }

    public void execute_csmt(CallableStatement cstmt, String succ, String err) throws SQLException{
      try{
        boolean hasresults = cstmt.execute();
        while(hasresults){
          ResultSet rs = cstmt.getResultSet();
          hasresults = cstmt.getMoreResults();
        }
        System.out.println(succ);
      }catch(SQLIntegrityConstraintViolationException e){
        System.out.println(err);
      }
    }

    public void print_statement(Connection conn, String statement) throws SQLException{
      Statement stmt = conn.createStatement();
      ResultSet rset = stmt.executeQuery(statement);
      ResultSetMetaData rsmd = rset.getMetaData();
      int columnsNumber = rsmd.getColumnCount();
      for(int i = 1; i <= columnsNumber; i++){
        System.out.print(rsmd.getColumnLabel(i)+"\t");
      }
      System.out.println();
      while (rset.next()) {
          for (int i = 1; i <= columnsNumber; i++) {
              String columnValue = rset.getString(i);
              try{
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                formatter.parse(columnValue);
                columnValue = columnValue.substring(0,10);
              }catch(ParseException p){}
              System.out.print(columnValue + "\t");
          }
          System.out.println("");
      }
    }

    public int signin(){
      System.out.println("Enter Oracle user id: ");
      user_id = scan.nextLine();
      System.out.println("Enter Oracle password for " + user_id + ": ");
      passwd = scan.nextLine();
      System.out.println("U_ID: "+user_id+" PASS: "+passwd);
      prop_man = new pm(this);
      bus_man = new bm(this);
      numa_man = new numa(this);
      ten = new tenant(this);
      try (Connection conn = DriverManager.getConnection(
        "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user_id, passwd);)
      {}catch (SQLException sqle) {
        System.out.println("Invalid username or password, please retry");
        return -1;
      }
      return 0;
    }

    public int submenu(){
      System.out.println("Choose the interface you want");
      System.out.println("(0) Property Manager");
      System.out.println("(1) Tenant");
      System.out.println("(2) NUMA Manager");
      System.out.println("(3) Business Manager");
      System.out.println("(4) Exit");
      int choice = get_int_in_range(0,4);
      try (Connection conn = DriverManager.getConnection(
        "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user_id, passwd);)
      {
        switch(choice){
          case 0: System.out.println("List of properties");
                  print_statement(conn, "select * from property order by pid asc");
                  System.out.println("Enter property id to be managed");
                  int pid = int_input(conn.prepareStatement("select count(*) from property where pid=?"),"PID");
                  if(pid==0){
                    choice = -1;
                  }
                  while(choice==0){
                    choice = prop_man.pm_menu(conn,pid);
                  }
            break;
          case 1: System.out.println("List of tenants");
                  print_statement(conn, "select * from tenant order by tid asc");
                  System.out.println("List of prospective tenants");
                  print_statement(conn, "select * from prospective order by tid asc");
                  System.out.println("Enter tenant id to be managed");
                  int tid = int_input(conn.prepareStatement("select count(*) from (select tid from tenant UNION select tid from prospective) where tid=?"),"TID");
                  if(tid==0){
                    choice = -1;
                  }
                  boolean tenant = true;
                  PreparedStatement pstmt = conn.prepareStatement("select count(*) from prospective where tid=?");
                  pstmt.setInt(1,tid);
                  ResultSet rset = pstmt.executeQuery();
                  if(rset.next()){
                    if(!rset.getString("count(*)").equals("0")){
                      tenant = false;
                    }
                  }
                  while(choice==1){
                    if(tenant){
                      choice = ten.tenant_menu(conn,tid);
                    }
                    else{
                      choice = ten.pros_menu(conn,tid);
                    }
                  }
            break;
          case 2: 
                  while(choice==2){
                    choice = numa_man.numa_menu(conn);
                  }
            break;
          case 3: 
                  while(choice==3){
                    choice = bus_man.business_menu(conn);
                  }
            break;
          case 4: choice = 0;
            break;
        }
      }
      catch (SQLException sqle) {
        System.out.println("SQLException : " + sqle);
      }
      return choice;
    }

    public static void main(String[] argv){
      ui menu = new ui();
      while(menu.signin()!=0){}
      while(menu.submenu()!=0){};
    }
}
