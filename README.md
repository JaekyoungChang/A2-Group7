# A2-Group7

Please note: This guide assumes that you are using the Windows 10 operating system and have a working version of Java v.8 running on your machine. If you do not yet have java correctly set up on your machine, please do this before continuing.

## Compile the program

To compile the program, navigate to the A2-Group7-main folder (The same folder that contains this document) extracted from the zip file on your computer. This can be done in Command Prompt or in Windows Explorer.

If you used Windows Explorer: 
	- Please click into the folder navigation bar at the top of the Explorer window and copy the path shown.
	- Open Command Prompt by opening the Start menu and typing cmd then choose Command Prompt from the options shown.
	- Type cd and then paste the path you copied from Windows Explorer into Command Prompt and then hit enter.

Type or copy the following commands into Command Prompt from within A2-Group7-main: javac src\cinco\ticket\*.java -d bin\

## Run the program

To run the program, please type or copy the following command into Command Prompt from within A2-Group7-main: java -cp .\bin cinco.ticket.Main

## Use the program

To use this program, you will need to type commands as prompted by the text menus and forms.

> Note 3: While using the program, you can type x at any time to return to the previous menu.

### The login menu

When you launch the program, you will see the Main Menu. From here you can manage accounts and log in to progress to the Ticket Menu. You'll be able to choose from the following options:

1. Login
2. Reset Password
3. Create Account
4. Exit

### Log in to the system

You must log in with your credentials in order to progress to the Ticket Menu
1. Choose "1. Login" from the menu.
2. Enter your name exactly as you typed it when you created your account.
3. Enter the password you set during account creation.

Note 4: For testing purposes, there is a test account:
	Username: test
	Password: 123

### Reset your password

If you ever forget your password, you can reset it here.

1. Choose "2. Reset Password" from the menu.
2. Enter your name exactly as you typed it when you created your account.
3. Enter a new password.

> Note 4: Please be aware that the same password requirements apply for a reset password as for initial password creation (refer to Note 6 below).

### Create an account

Before you can Log in to the system, you must first create an account.

1. Choose "3. Create account" from the login menu.
3. Enter your name.
4. Enter your email address (this must be a valid email address).
5. Enter your contact phone number.
6. Enter the password that you would like to use (refer to Note 6).

Your account will be created with a unique id and you'll be able to log in immediately.

> Note 5: Staff that require the Technician role must be added to the system by an administrator. If you require this kind of access, please contact your administrator.

> Note 6: Your password must be at least 20 characters in length and contain Upper and Lowercase characters and a number.

### The ticket menu

Once you are logged in, you will arrive at the ticket menu, where you can choose from the following options:

1. List Tickets
2. Create Ticket
3. Logout
4. Exit

### List tickets

You can view a list of tickets which are relevant to you. 

1. Choose "1. List Tickets" from the ticket menu.

This option will provide you with a list of all of your currently active tickets, along with the following information:
	- Description of the ticket
	- Current status of the ticket
	- Current severity of the ticket
	- The name of the Technician who is currently assigned to the ticket.

### Create a ticket

You can submit a ticket describing an issue you are having which needs to be triaged by a technician.

1. Choose "2. Create Ticket" from the ticket menu.
1. Enter a description of the issue.
2. Enter a severity level for the issue (from 1 to 3, where 1 is low and 3 is high).

> Note 7: Your ticket will be automatically assigned to an appropriate technician and a unique ID will be generated automatically.

### Log out of the system

When you are done, you can log out to return to [the login menu](#the-login-menu).

1. Choose "3. Logout" from the ticket menu.

### Exit the system

If you would like to exit the program, simply choose the option "4. Exit" from any of the menus.

### Technician Logins

Five Technicians have been set up, these are:

Level 1:
Harry Styles (Password: 123)
Niall Horan (Password: 123)
Liam Payne (Password: 123)

Level 2:
Louis Tomlinson (Password: 123)
Zayn Malik (Password: 123)

After logging in as a Technician user, you will see the Technician Menu which has the following options:

1. List Tickets
2. Update Ticket Status
3. Update Ticket Severity
4. Generate Report
5. Logout
6. Exit

### List Tickets

This option will provide you with a list of all of the tickets currently assigned to you. It will also include the following information:
	- The unique identifier of the ticket
	- A description of the issue
	- The current status of the ticket
	- The current severity of the ticket
	- The name of the user who submitted the ticket
	- When the ticket was created
	- When the ticket was last updated

### Update Ticket Status

This option will give you the opportunity to change the status of a ticket assigned to you.

When you choose this option, you will be presented with a list of all of the tickets currently assigned to you. The system will then request that you enter the ticket ID from the list. You may do this by typing the ID or by copying the ID from the list and pasting it into the correct position. 

You may then choose the following options from a list:

1. Open
2. Closed (Resolved)
3. Closed (Unresolved)

Note 8: Tickets may only be reopened if less than 24 hours have passed since the ticket was marked as Closed (Resolved) or Closed (Unresolved)
Note 9: After a ticket has been marked as Closed (Resolved) or Closed (Unresolved) for more than 24 hours the system will automatically send the ticket to the archive the next time the program is run.

### Update Ticket Severity

This option will give you the option of changing the severity of a ticket assigned to you.

When you choose this option, you will be presented with a list of all of the tickets currently assigned to you. The system will then request that you enter the ticket ID from the list. You may do this by typing the ID or by copying the ID from the list and pasting it into the correct position. 

You may then choose the following options from a list:
1. Low
2. Medium
3. High

Note: 9: If you choose a severity that is not handled by your service desk, the ticket will be reallocated to a Technician from the appropriate service desk.

## Generate Report

This option gives you the opportunity to create a report that shows all tickets that are currently active in the system.

On choosing this option, you will be asked to choose a time period for the report. The options you may choose from are:
1. 1 Day
2. 2 Days
3. 1 Week
4. 2 Weeks

After choosing a time period, you will be presented with a report that shows:
	- The number of tickets that have been submitted during the specified period.
	- The number of tickets that have been resolved
	- the number of tickets that remain outstanding

You will also be shown a list of all of the resolved and outstanding tickets including the name of the user they were submitted by and when they were created.
