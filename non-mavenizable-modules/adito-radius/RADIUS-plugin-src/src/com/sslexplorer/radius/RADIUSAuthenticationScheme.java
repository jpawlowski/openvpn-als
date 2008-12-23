/* HEADER */
package com.sslexplorer.radius;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import net.sourceforge.jradiusclient.RadiusAttribute;
import net.sourceforge.jradiusclient.RadiusAttributeValues;
import net.sourceforge.jradiusclient.RadiusClient;
import net.sourceforge.jradiusclient.RadiusPacket;
import net.sourceforge.jradiusclient.exception.InvalidParameterException;
import net.sourceforge.jradiusclient.exception.RadiusException;
import net.sourceforge.jradiusclient.util.ChapUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sslexplorer.boot.ContextHolder;
import com.sslexplorer.core.CoreServlet;
import com.sslexplorer.security.AccountLockedException;
import com.sslexplorer.security.InvalidLoginCredentialsException;
import com.sslexplorer.security.PasswordAuthenticationScheme;
import com.sslexplorer.security.User;
import com.sslexplorer.security.UserDatabaseException;

/**
 * @author Brett Smith <brett@3sp.com>
 */
public class RADIUSAuthenticationScheme extends PasswordAuthenticationScheme {
    
    final static Log log = LogFactory.getLog(RADIUSAuthenticationScheme.class);

    private String serverHostName;
    private int authPort;
    private int acctPort;
    private String sharedSecret;
    
    /**
     * 
     */
    public RADIUSAuthenticationScheme() {
        super();
    }
    
    protected User doLogon(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException, AccountLockedException {
        
        // set up RADIUS client to do authentication
        RadiusClient rc = null;
        try{
            rc = new RadiusClient(serverHostName, authPort, acctPort, sharedSecret);
        }catch(RadiusException rex){
            log.error("RadiusException: " + rex.getMessage());
        }catch(InvalidParameterException ivpex){
            log.error("Unable to create Radius Client due to invalid parameter! " +
                    "InvalidParameterException: " + ivpex.getMessage());
        }
        ChapUtil chapUtil = new ChapUtil();
        if(radiusAuthenticate(rc, chapUtil, username, password)){
            log.debug("RADIUS Validation SUCCEEDED!");
            try {
                User user = CoreServlet.getServlet().getUserDatabase().getAccount(null, username); 
                return user; 
            } catch (Exception e) {
                throw new UserDatabaseException("Failed to get user account.", e);
            }
        }else{
            log.warn("RADIUS Validation FAILED!");
            throw new InvalidLoginCredentialsException("Bad username or password.");
            
        }
        
    }
    

    
    private boolean radiusAuthenticate(final RadiusClient rc,
          final ChapUtil chapUtil,
          String radUser, String radPass){
        
          boolean authenticated = false;
      
      try{
          log.info("Entering RADIUS Validation...");
          
          RadiusPacket accessRequest = new RadiusPacket(RadiusPacket.ACCESS_REQUEST);
          RadiusAttribute userNameAttribute;
          
          userNameAttribute = new RadiusAttribute(RadiusAttributeValues.USER_NAME, radUser.getBytes());
          accessRequest.setAttribute(userNameAttribute);
                  
          accessRequest.setAttribute(new RadiusAttribute(RadiusAttributeValues.USER_PASSWORD, radPass.getBytes()));
              
          RadiusPacket accessResponse = rc.authenticate(accessRequest);
          switch(accessResponse.getPacketType()){
              case RadiusPacket.ACCESS_ACCEPT:
                  log.debug("User " + radUser + " validated by RADIUS server");
                  setRADIUSAttributes(accessResponse);
                  authenticated = true;
                  break;
              case RadiusPacket.ACCESS_REJECT:
                  log.warn("User " + radUser + " NOT validated by RADIUS server");
                  authenticated = false;
                  break;
              case RadiusPacket.ACCESS_CHALLENGE:
                  String reply = new String(accessResponse.getAttribute(RadiusAttributeValues.REPLY_MESSAGE).getValue());
                  log.debug("User " + radUser + " Challenged with " + reply);
                  break;
              default:
                  log.warn("Whoa, what kind of RadiusPacket is this? - " + accessResponse.getPacketType());
                  break;
          }
                      
      }catch(InvalidParameterException ivpex){
          log.info("InvalidParameterException: " + ivpex.getMessage());
          authenticated = false;
      }catch(RadiusException rex){
          log.info("RadiusException: " + rex.getMessage());
          authenticated = false;
      }catch(IOException ioex){
          log.info("IOException: " + ioex.getMessage());
          authenticated = false;
      }
      
      return authenticated;
    }

    /* (non-Javadoc)
     * @see com.sslexplorer.security.AuthenticationScheme#init(javax.servlet.http.HttpSession)
     */
    public void init(HttpSession servletSession) {
        super.init(servletSession);

        // Get RADIUS server properties
        try {
            serverHostName = ContextHolder.getContext().getPropertyDatabase().getProperty(0, null, "radius.serverHostName").toLowerCase().trim();
            authPort = Integer.parseInt(ContextHolder.getContext().getPropertyDatabase().getProperty(0, null, "radius.authenticationPort").toUpperCase().trim());
            acctPort = Integer.parseInt(ContextHolder.getContext().getPropertyDatabase().getProperty(0, null, "radius.accountingPort").toUpperCase().trim());
            sharedSecret = ContextHolder.getContext().getPropertyDatabase().getProperty(0, null, "radius.sharedSecret").trim();
            
            log.debug("Setting RADIUS server to: " + serverHostName);
            log.debug("Setting RADIUS authentication port to: " + authPort);
            log.debug("Setting RADIUS accounting port to: " + acctPort);
            log.debug("Setting RADIUS shared secret!");
        }
        catch(Exception e) {
            log.error("Failed to get RADIUS configuration. Authentication will fail.", e);
        }
    }
    
    private void setRADIUSAttributes(RadiusPacket rp) throws IOException{
      
      Iterator attributes = rp.getAttributes().iterator();
      RadiusAttribute tempRa;
      
      log.info("Response Packet Attributes Table: ");
      log.info("\tValue\t\tType");
      
      while(attributes.hasNext()){
          tempRa = (RadiusAttribute)attributes.next();
          log.info("\t" + tempRa.getType() + "\t\t" + new String(tempRa.getValue()));
      }
      
    }
}
