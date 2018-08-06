package view;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import javax.faces.context.FacesContext;

import javax.naming.InitialContext;

import javax.sql.DataSource;

import oracle.adf.share.ADFContext;

public class Login {
    private String username;
    private String password;
    
    public Login() {
        username = "";
        password = "";
    }

    public String login() {
        String username = getUsername();
        String password = getPassword();
        CallableStatement cst = null;
        Connection conn = null;
        try{
            InitialContext initialContext = new InitialContext();
            DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/connDS");
            conn = ds.getConnection();
            cst = conn.prepareCall("begin ? := " + "xx_cust_auth.login(?,?)" + "; end;");
            cst.registerOutParameter(1, Types.VARCHAR);
            cst.setObject(2, username);
            cst.setObject(3, password);
            cst.executeUpdate();
            int returnFromMethod = cst.getInt(1);
            System.out.println("return from callable procedure: "+returnFromMethod);
            if (returnFromMethod != -1){
                ADFContext.getCurrent().getPageFlowScope().put("status", returnFromMethod);
                ADFContext.getCurrent().getPageFlowScope().put("username", getUsername());
            }else{
                ADFContext.getCurrent().getPageFlowScope().put("status", -1);
                ADFContext.getCurrent().getPageFlowScope().put("username", "nan");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                if (cst != null){
                    cst.close();
                }
                if (conn != null){
                    conn.close();
                }
            }catch(Exception ie){
                ie.printStackTrace();
            }
        }
        
        
        return null;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    
   
}
