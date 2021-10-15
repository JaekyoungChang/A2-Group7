# A2-Group7

## Compile the program

To compile the program please run the "compile" script from within the "bin" directory of this project.

> Note 1: you will have to select the appropriate "compile" script for your operating system - `compile.bat` is for a Windows-based OS and `compile.sh` is for a Linux-based OS. If you are using a Linux-based OS, you may need to make the script executable first by running `chmod +x bin/compile.sh`.

If you would like to manually compile the program, simply follow the commands in the "compile" script.

## Run the program

To run the program please run the "run" script from within the "bin" directory of this project.

> Note 2: you will have to select the appropriate "run" script for your operating system - `run.bat` is for a Windows-based OS and `run.sh` is for a Linux-based OS. If you are using a Linux-based OS, you may need to make the script executable first by running `chmod +x bin/run.sh`.*

If you would like to manually run the program, simply follow the commands in the "run" script.

## Use the program

To use this program, you will need to type commands as prompted by the text menus and forms.

> Note 3: While using the program, you can type x at any time to return to the previous menu.

### The login menu

When you launch the program, you will see the login menu. From here you can manage accounts and [log in](#log-in-to-the-system) to progress to [the ticket menu](#the-ticket-menu). You'll be able to choose from the following options:

1. [Login](#log-in-to-the-system)
2. [Reset Password](#reset-your-password)
3. [Create Account](#create-an-account)
4. [Exit](#exit-the-system)

### Log in to the system

You must log in with your credentials in order to progress to [the ticket menu](#the-ticket-menu).

1. Choose "1. Login" from the menu.
2. Enter your name exactly as you typed it when you created your account.
3. Enter the password you set during account creation.

### Reset your password

If you ever forget your password, you can reset it here.

1. Choose "2. Reset Password" from the menu.
2. Enter your name exactly as you typed it when you created your account.
3. Enter a new password.

> Note 4: Please be aware that the same password requirements apply for a reset password as for initial password creation (refer to Note 6 below).

### Create an account

Before you can [log in](#log-in-to-the-system), you must first create an account.

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

1. [List Tickets](#list-tickets)
2. [Create Ticket](#create-a-ticket)
3. [Logout](#log-out-of-the-system)
4. [Exit](#exit-the-system)

### List tickets

You can view a list of tickets which are relevant to you. 


1. Choose "1. List Tickets" from the ticket menu.

What is displayed will change depending on your role; for regular staff, this will be the list of tickets you have submitted, whereas for technicians, this will be the list of tickets assigned to you.

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
