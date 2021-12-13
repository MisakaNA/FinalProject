**Project Name:** Reminder Manager  
**Purpose:** CS-GY 9053 Final Project  
**Developer:** Yumeng Sun  
**Organization:** NYU Tandon Schools of Engineering  
**NetID:** ys5046  
**Project Link:**  

----------------------------------------------------------------
**How to run**  
1. To execute this project correctly, you must have `JDK 1.8` and `Javafx 8` installed and you also need `sqlite-jdbc-3.30.1.jar` include in the project library.  
2. First run `Server.java` to start the server, client window will not show if server is not started.  
3. Then run `Main.java` to start a client. Multiple clients can be run at the same time, but you need to set it up in the configuration. For **IntelliJ IDEA**, at the right top of the window, you can `Edit Configuration` -> `Modify options` and check `Allow Multiple Instances`.  

----------------------------------------------------------------
**Main Features**  
- __User__
    1. Log in or register a user account with user name and password
    2. Add, edit, or delete a Reminder
    3. A user can log in the account from multiple client windows at the same time, but need to `refresh` the result TextArea by clicking `refresh` at right top of the client window after doing any changes at the other client windows.
    4. Log out the user account
    
- __Administrator__
    1. There is one and only one administrator in this project and it is presetted. 
    2. The user name for administrator is `admin` and the password is also `admin`  
    3. The administrator can delete a user account by `UID`, when a user got deleted, **all related reminders of that account will also be deleted**.  

----------------------------------------------------------------
**Copyright**

This project is developed by Yumeng Sun, all development process are finished on 12/13/2021. **AGPL-3.0 License** is set for this project.  

For any douts, bugs and questions, please contact Yumeng Sun at ys5046@nyu.edu.  
