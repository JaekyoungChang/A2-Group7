import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	static Account user = null;
	static Scanner sc = new Scanner(System.in);
	static ArrayList<Account> accounts = new ArrayList<Account>();

	public static void login() {
	}
	
	public static void resetPassword() {
	}

	public static void createAccount() {

		System.out.println("*** CREATE ACCOUNT ***");
		System.out.print("Enter Email: ");
		String email = sc.nextLine();
		System.out.print("Enter Name: ");
		String name = sc.nextLine();
		System.out.print("Enter Phone: ");
		String phone = sc.nextLine();
		System.out.print("Enter Password: ");
		String password = sc.nextLine();

		boolean validPassword = passwordValidation(password);

		while (validPassword == false) {
			System.out.print("Enter Password:");
			password = sc.nextLine();
			validPassword = passwordValidation(password);
		}

		user = new Account(email, name, phone, password);
		accounts.add(user);
		System.out.println("");
		System.out.println("Account Created.\n");
	}

	public static boolean passwordValidation(String password) {

		if (password.length() > 20) {
			System.out.println("");
			System.out.println("Invalid password! Maxmimum password length is 20 characters.");
			return false;
		}

		boolean containsUppercase = false;
		boolean containsLowercase = false;
		boolean containsNumber = false;

		for (int i = 0; i < password.length(); i++) {

			Character c = password.charAt(i);

			if (Character.isUpperCase(c)) {
				containsUppercase = true;
			} else if (Character.isLowerCase(c)) {
				containsLowercase = true;
			} else if (Character.isDigit(c)) {
				containsNumber = true;
			}

			if (containsLowercase == true && containsUppercase == true && containsNumber == true) {
				return true;
			}
		}
		
		System.out.println("");
		System.out.println("Invalid password! Password must contain a number, uppercase and lowercase letter.");
		return false;
	}

	public static void main(String[] args) {

		String choice;

		do {
			System.out.println("*** MENU ***");
			System.out.println("Select from the following options:");
			System.out.println("1. Login");
			System.out.println("2. Forgot Password");
			System.out.println("3. Create Account");
			System.out.println("4. Exit");

			choice = sc.nextLine();

			switch (choice) {
			case "1":
				// login();
				break;

			case "2":
				// resetPassword();
				break;

			case "3":
				createAccount();
				break;

			case "4":
				System.out.println("Exiting program...");
				System.exit(0);
				break;

			default:
				   System.out.println("Invalid choice. Please enter a number from 1-4.\n");
			}

			System.out.println();
		} while (choice != "4");
	}
}