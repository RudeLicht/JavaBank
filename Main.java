import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    public static Scanner scan = new Scanner(System.in);
    public static String userName;
    public static String enteredEmail;

    public static void main(String[] args) {
        clear(0);
        mainMenu();
    }

    public static void otpSettings() {
        JSONArray list = readJSONArrayFromFile("data/user.json");
        int x = 0;
        for (Object object : list) {
            JSONObject userObject = (JSONObject) object;
            String userEmail = (String) userObject.get("Email");
            boolean userOTP = (boolean) userObject.get("OTP");
            if (userEmail.toLowerCase().equals(enteredEmail)) {
                if (userOTP) {
                    int choice;
                    do {
                        clear(0);
                        System.out.println("\tOTP Settings\n");
                        System.out.println("Your OTP is: " + (userOTP ? "Enabled" : "Disabled\n"));
                        System.out.println("""
                                [1] Disable OTP
                                [2] Back
                                """);
                        System.out.print("Please enter your choice: ");
                        choice = intWhileFunc();
                        switch (choice) {
                            case 1:
                                userObject.replace("OTP", false);
                                list.set(x, userObject);
                                writeJSONArrayToFile(list, "data/user.json");
                                clear(0);
                                System.out.println("Your OTP is now Disabled!");
                                wait(1000);
                                return;
                            case 2:
                                return;
                            default:
                                System.out.println("Invalid argument, Please try again!");
                        }
                    } while (choice != 2);
                } else {
                    int choice;
                    do {
                        clear(0);
                        System.out.println("\tOTP Settings\n");
                        System.out.println("""
                                [1] Enable OTP
                                [2] Back
                                """);
                        System.out.print("Please enter your choice: ");
                        choice = intWhileFunc();

                        switch (choice) {
                            case 1:
                                userObject.replace("OTP", true);
                                list.set(x, userObject);
                                writeJSONArrayToFile(list, "data/user.json");
                                clear(0);
                                System.out.println("Your OTP is now Enabled!");
                                wait(1000);
                                return;
                            case 2:
                                return;
                            default:
                                System.out.println("Invalid argument, Please try again!");
                        }
                    } while (choice != 2);
                }
            }
            x++;
        }
    }

    public static void myAccount() {
        clear(0);
        JSONArray list = readJSONArrayFromFile("data/user.json");
        for (Object object : list) {
            JSONObject userObject = (JSONObject) object;
            String fName = (String) userObject.get("First Name");
            String lName = (String) userObject.get("Last Name");
            String email = (String) userObject.get("Email");
            boolean otp = (boolean) userObject.get("OTP");
            String nationality = (String) userObject.get("Nationality");
            int phoneNumber = ((Long) userObject.get("Phone Number")).intValue();
            int amount = ((Long) userObject.get("Amount")).intValue();

            if (enteredEmail.toLowerCase().equals(email)) {
                int choice;
                do {
                    clear(0);
                    System.out.println("\tAccount Information\n");
                    System.out.println("Name: " + fName + " " + lName);
                    System.out.println("Email: " + email);
                    System.out.println("Phone Number: " + phoneNumber);
                    System.out.println("Nationality: " + nationality);
                    System.out.println("Balance: " + amount);
                    System.out.println("OTP: " + (otp ? "Enabled" : "Disabled"));
                    System.out.println("\n[1] Back");
                    System.out.print("\nPlease enter your choice: ");
                    choice = intWhileFunc();
                } while (choice != 1);
            }
        }

    }

    public static void mainMenu() {
        int choice;

        do {
            clear(0);
            System.out.println("""
                        \n\tJavaBank\n
                        [1] Login
                        [2] Register
                        [3] Quit
                    """);

            System.out.print("Enter your choice: ");
            choice = intWhileFunc();
            scan.nextLine();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("\nExiting JavaBank. Goodbye!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    public static void transfer() {
        clear(0);
        scan.nextLine();
        System.out.println("\tTransfer\n");

        System.out.print("Transfer To [Email]: ");
        String transferTo = stringWhileFunc(scan.nextLine());

        System.out.print("Amount: ");
        int desiredAmount = intWhileFunc();

        JSONArray list = readJSONArrayFromFile("data/user.json");

        for (int i = 0; i < list.size(); i++) {
            JSONObject userObject = (JSONObject) list.get(i);
            String userEmail = (String) userObject.get("Email");

            if (enteredEmail.toLowerCase().equals(userEmail)) {
                Long userBalance = (Long) userObject.get("Amount");

                if (userBalance - desiredAmount < 0) {
                    clear(0);
                    System.out.println("Insufficient balance. You have: " + userBalance);
                    wait(2000);
                    return;
                }

                for (int j = 0; j < list.size(); j++) {
                    JSONObject transferToObject = (JSONObject) list.get(j);
                    String transferToEmail = (String) transferToObject.get("Email");

                    if (transferTo.toLowerCase().equals(transferToEmail)) {
                        Long recipientBalance = (Long) transferToObject.get("Amount");
                        recipientBalance = recipientBalance + desiredAmount;
                        transferToObject.replace("Amount", recipientBalance);
                        list.set(j, transferToObject);
                        writeJSONArrayToFile(list, "data/user.json");

                        userBalance = userBalance - desiredAmount;
                        userObject.replace("Amount", userBalance);
                        list.set(i, userObject);
                        writeJSONArrayToFile(list, "data/user.json");

                        System.out.println("\nTransfer successful! Your new balance is: " + userBalance);
                        wait(2500);
                        return;
                    }
                }

                clear(0);
                System.out.println("Recipient not found.");
                wait(2000);
                return;
            }
        }

        clear(0);
        System.out.println("User with the entered email not found.");
        wait(2000);
    }

    public static void deposit() {
        clear(0);
        System.out.println("\tDeposit\n");
        System.out.print("Deposit Amount: ");
        int desiredAmount = intWhileFunc();
        JSONArray list = readJSONArrayFromFile("data/user.json");

        for (int i = 0; i < list.size(); i++) {
            JSONObject userObject = (JSONObject) list.get(i);
            String userEmail = (String) userObject.get("Email");

            if (enteredEmail.toLowerCase().equals(userEmail)) {
                Long amount = (Long) userObject.get("Amount");
                amount = amount + desiredAmount;
                userObject.replace("Amount", amount);
                list.set(i, userObject);
                writeJSONArrayToFile(list, "data/user.json");
                System.out.println("\nDeposit successful! Your new balance is: " + amount);
                wait(2500);
                return;
            }
        }

        System.out.println("\nUser with the entered email not found.");
        wait(2000);
    }

    public static void userMenu() {
        int choice;
        do {
            clear(0);
            System.out.println("\n\tHello " + userName + "\n");
            System.out.println("""
                    [1] Deposit
                    [2] Transfer
                    [3] OTP Settings
                    [4] My Account
                    [5] Change Account Information
                    [6] Logout
                    """);
            System.out.print("Please enter your choice: ");
            choice = intWhileFunc();
            switch (choice) {
                case 1:
                    deposit();
                    break;
                case 2:
                    transfer();
                    break;
                case 3:
                    otpSettings();
                    break;
                case 4:
                    myAccount();
                    break;
                case 5:
                    changeAccountInformation();
                    break;
                case 6:
                    System.out.println("\nLogging out...");
                    wait(500);
                    break;
                default:
                    System.out.println("\nInvalid choice, Please try again.");
                    break;
            }
        } while (choice != 6);
    }

    public static void changeAccountInformation() {

        JSONArray list = readJSONArrayFromFile("data/user.json");
        for (Object object : list) {
            JSONObject userObject = (JSONObject) object;
            String email = (String) userObject.get("Email");

            if (enteredEmail.toLowerCase().equals(email)) {
                int choice;
                do {
                    clear(0);
                    System.out.println("\tChange Account Information\n");
                    System.out.println("""
                            [1] First Name
                            [2] Last Name
                            [3] Phone Number
                            [4] Email
                            [5] Nationality
                            [6] Back
                            """);
                    System.out.print("Please enter your choice: ");
                    choice = intWhileFunc();
                    scan.nextLine();

                    switch (choice) {
                        case 1:
                            System.out.print("New First Name: ");
                            String newFirstName = stringWhileFunc(scan.nextLine());
                            userObject.replace("First Name", newFirstName.toLowerCase());
                            break;
                        case 2:
                            System.out.print("New Last Name: ");
                            String newLastName = stringWhileFunc(scan.nextLine());
                            userObject.replace("Last Name", newLastName.toLowerCase());
                            break;
                        case 3:
                            System.out.print("New Phone Number: ");
                            int newPhoneNumber = intWhileFunc();
                            userObject.replace("Phone Number", newPhoneNumber);
                            break;
                        case 4:
                            System.out.print("New Email: ");
                            String newEmail = stringWhileFunc(scan.nextLine());
                            if (isEmailTaken(newEmail, list)) {
                                System.out.println("Email is already taken. Please choose a different one.");
                                wait(2000);
                                break;
                            }
                            userObject.replace("Email", newEmail.toLowerCase());
                            enteredEmail = newEmail;
                            break;
                        case 5:
                            System.out.print("New Nationality: ");
                            String newNationality = stringWhileFunc(scan.nextLine());
                            userObject.replace("Nationality", newNationality.toLowerCase());
                            break;
                        case 6:
                            writeJSONArrayToFile(list, "data/user.json");
                            return;
                        default:
                            System.out.println("\nInvalid choice. Please try again.");
                    }
                } while (true);
            }
        }
    }

    private static boolean isEmailTaken(String email, JSONArray userList) {
        for (Object object : userList) {
            JSONObject userObject = (JSONObject) object;
            String existingEmail = (String) userObject.get("Email");
            if (email.equalsIgnoreCase(existingEmail)) {
                return true;
            }
        }
        return false;
    }

    public static void login() {
        clear(0);
        JSONArray list = readJSONArrayFromFile("data/user.json");
        System.out.println("\tLogin\n");
        System.out.print("Enter Email: ");
        enteredEmail = scan.nextLine();

        System.out.print("Enter Password: ");
        String enteredPassword = scan.nextLine();

        for (Object object : list) {
            JSONObject userObject = (JSONObject) object;
            String userEmail = (String) userObject.get("Email");
            userName = (String) userObject.get("First Name");
            boolean otp = (boolean) userObject.get("OTP");

            if (enteredEmail.toLowerCase().equals(userEmail)) {
                Object storedPasswordObj = userObject.get("Password");
                if (storedPasswordObj != null) {
                    String storedEncodedPassword = storedPasswordObj.toString();

                    byte[] decodedBytes = Base64.getDecoder().decode(storedEncodedPassword);
                    String storedPassword = new String(decodedBytes);

                    if (enteredPassword.equals(storedPassword)) {
                        if (otp) {
                            clear(0);
                            Random random = new Random();
                            int OTP = random.nextInt(1000, 9999);
                            System.out.println("\nYour OTP code: " + OTP);
                            System.out.print("Please enter your OTP code: ");
                            if (scan.hasNextInt()) {
                                if (scan.nextInt() == OTP) {
                                    System.out.println("\nLogin successful!");
                                    clear(1);
                                    userMenu();
                                    return;
                                } else {
                                    System.out.println("\nIncorrect OTP, Aborting!");
                                    wait(2000);
                                    return;
                                }
                            } else {
                                System.out.println("\nIncorrect OTP, Aborting!");
                                wait(2000);
                                return;
                            }
                        } else {
                            System.out.println("\nLogin successful!");
                            clear(1);
                            userMenu();
                            return;
                        }
                    } else {
                        System.out.println("\nIncorrect password. Please try again.");
                        wait(2000);
                        return;
                    }
                } else {
                    System.out.println("\nPassword not found for the user. Please try again.");
                    wait(2000);
                    return;
                }
            }
        }

        System.out.println("\nUser with the entered email not found. Please register.");
        wait(2000);
    }

    public static void register() {
        clear(0);
        System.out.println("\tRegister\n");
        System.out.print("First Name: ");
        String fName = stringWhileFunc(scan.nextLine());

        System.out.print("Last Name: ");
        String lName = stringWhileFunc(scan.nextLine());

        System.out.print("Email: ");
        String email = stringWhileFunc(scan.nextLine());

        System.out.print("Password: ");
        String password = stringWhileFunc(scan.nextLine());

        System.out.print("Phone Number [eg., 1234567890]: ");
        int phoneNumber = intWhileFunc();

        scan.nextLine();

        System.out.print("Nationality: ");
        String nationality = stringWhileFunc(scan.nextLine());

        JSONArray jsonArray = readJSONArrayFromFile("data/user.json");
        for (Object object : jsonArray) {
            JSONObject userObject = (JSONObject) object;
            String checkForEmail = (String) userObject.get("Email");
            if (checkForEmail.toLowerCase().equals(email)) {
                clear(0);
                System.out.println("\nThe email " + email + "is already registered!");
                wait(3000);
                return;
            }
        }
        JSONObject newObject = new JSONObject();
        newObject.put("id", jsonArray.size() == 0 ? 1 : jsonArray.size() + 1);
        newObject.put("First Name", fName.toLowerCase());
        newObject.put("Last Name", lName.toLowerCase());
        newObject.put("Password", Base64.getEncoder().encodeToString(password.getBytes()));
        newObject.put("Email", email.toLowerCase());
        newObject.put("Phone Number", phoneNumber);
        newObject.put("Nationality", nationality.toLowerCase());
        newObject.put("Amount", 2000);
        newObject.put("OTP", false);

        jsonArray.add(newObject);

        writeJSONArrayToFile(jsonArray, "data/user.json");

        System.out.println("Successfully registered!");
        clear(1);

    }

    private static JSONArray readJSONArrayFromFile(String filePath) {
        try (FileReader fileReader = new FileReader(filePath)) {
            JSONParser jsonParser = new JSONParser();
            return (JSONArray) jsonParser.parse(fileReader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static void writeJSONArrayToFile(JSONArray jsonArray, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            jsonArray.writeJSONString(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String stringWhileFunc(String s) {
        while (s.isEmpty()) {
            System.out.print("Invalid argument: ");
            s = scan.nextLine();
        }
        return s;
    }

    public static int intWhileFunc() {
        while (true) {
            while (!scan.hasNextInt()) {
                System.out.print("Invalid argument: ");
                scan.next();
            }
            return scan.nextInt();
        }
    }

    public static void clear(int x) {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                if (x == 1) {
                    wait(1000);
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    return;
                }
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void wait(int x) {
        try {
            Thread.sleep(x);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
