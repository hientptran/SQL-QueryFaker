package ikg.QueryFaker;

import com.github.javafaker.Faker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static void main(String[] args) {
        writeToFile();
    }
    //-----------------------------------------------------------------
    static Faker faker = new Faker(Locale.GERMANY);
    static Scanner s = new Scanner(System.in);
    static String[] types = {"full address", "street address", "zip code", "boolean", "price", "product name", "company name", "date", "ingredient", "first name", "last name", "full name", "username", "number", "cell phone", "phone"};
    static List<String> typesList = Arrays.asList(types);
    //-----------------------------------------------------------------
    static String readString(String text, String pattern) {
        String result;
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        while (true) {
            try {
                System.out.println(text);
                result = s.nextLine();
                Matcher matcher = p.matcher(result);
                if (matcher.find() || result.equals("")) {
                    break;
                }
                else {
                    System.out.println("Invalid input. Try again :)");
                }
            } catch (Exception e) {
                s.nextLine();
                System.out.println("Invalid input.");
                e.printStackTrace();
            }
        }
        return result.trim();
    }
    static int readInt(String text) {
        int result;
        while (true) {
            try {
                System.out.println(text);
                result = s.nextInt();
                s.nextLine();
                break;
            } catch (Exception e) {
                s.nextLine();
                System.out.println("Invalid input. Try again :)");
            }
        }
        return result;
    }

    //-----------------------------------------------------------------
    public static String createQueryFormat(String[] data) {
        StringBuilder result = new StringBuilder();
        if (data.length > 1) {
            result.append(String.format("('%s', ", data[0]));
            for (int i = 1; i < data.length - 1; i++) {
                result.append(String.format("'%s', ", data[i]));
            }
            result.append(String.format("'%s')", data[data.length - 1]));
        }
        else if (data.length == 1) {
            result.append(String.format("'%s')", data[0]));
        }
        return result.toString();
    }
    public static String[] typeInput() {
        System.out.println("""
                - Address: full address, street address, zip code
                - Boolean: boolean
                - Commerce: price, product name
                - Company: company name
                - Date: date
                - Food: ingredient
                - Name: first name, last name, full name, username
                - Number: number
                - Phone number: cell phone, phone number""".indent(6));
        String str;
        while (true) {
            try {
                System.out.println("What do you want to generate (comma-seperated)?: ");
                str = s.nextLine();
                str = str.trim();
                String[] parts = str.split(",");
                String[] cleanParts = new String[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    cleanParts[i] = parts[i].trim().toLowerCase();
                }
                for (String part : cleanParts) {
                    if (!typesList.contains(part)) {
                        throw new Exception("Invalid type. Try again :)");
                    }
                }
                String[] fullParts = new String[cleanParts.length];
                for (int i = 0; i < cleanParts.length; i++) {
                    if (cleanParts[i].contains("date")) {
                        System.out.println("Additional information for field with type Date needed.");
                        String fromDateStr = readString("Enter lower limit: ", "^(19|20)\\d\\d[- \\/.](0[1-9]|1[012])[- \\/.](0[1-9]|[12][0-9]|3[01])$");
                        String toDateStr = readString("Enter upper limit: ", "^(19|20)\\d\\d[- \\/.](0[1-9]|1[012])[- \\/.](0[1-9]|[12][0-9]|3[01])$");
                        fullParts[i] = cleanParts[i] + ", " + fromDateStr + ", " + toDateStr;
                    }
                    else if (cleanParts[i].contains("number")) {
                        System.out.println("Additional information for field with type Number needed.");
                        int digits = readInt("Enter number of digits: ");
                        fullParts[i] = cleanParts[i] + ", " + digits;
                    }
                    else {
                        fullParts[i] = cleanParts[i];
                    }
                }
                return fullParts;
            } catch (Exception e) {
                //s.nextLine();
                e.printStackTrace();
            }
        }
    }
    public static String[] generateRow(String[] typeInput) throws ParseException {
        String[] parts = new String[typeInput.length];
        for (int i = 0; i < typeInput.length; i++) {
            if (typeInput[i].contains("full address")) {
                parts[i] = faker.address().fullAddress();
            }
            else if (typeInput[i].contains("street address")) {
                parts[i] = faker.address().streetAddress();
            }
            else if (typeInput[i].contains("zip code")) {
                parts[i] = faker.address().zipCode();
            }
            else if (typeInput[i].contains("boolean")) {
                parts[i] = String.valueOf(faker.bool().bool());
            }
            else if (typeInput[i].contains("price")) {
                parts[i] = faker.commerce().price();
            }
            else if (typeInput[i].contains("product name")) {
                parts[i] = faker.commerce().productName();
            }
            else if (typeInput[i].contains("company name")) {
                parts[i] = faker.company().name();
            }
            else if (typeInput[i].contains("date")) {
                String[] dateParts = typeInput[i].split(",");
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date fromDate = dateFormatter.parse(dateParts[1].trim());
                Date toDate = dateFormatter.parse(dateParts[2].trim());
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String s = formatter.format(faker.date().between(fromDate, toDate));
                parts[i] = s;
            }
            else if (typeInput[i].contains("ingredient")) {
                parts[i] = faker.food().ingredient();
            }
            else if (typeInput[i].contains("first name")) {
                parts[i] = faker.name().firstName();
            }
            else if (typeInput[i].contains("last name")) {
                parts[i] = faker.name().lastName();
            }
            else if (typeInput[i].contains("full name")) {
                parts[i] = faker.name().fullName();
            }
            else if (typeInput[i].contains("username")) {
                parts[i] = faker.name().username();
            }
            else if (typeInput[i].contains("number")) {
                String[] numberParts = typeInput[i].split(",");
                int digits = Integer.parseInt(numberParts[1].trim());
                parts[i] = String.valueOf(faker.number().randomNumber(digits, true));
            }
            else if (typeInput[i].contains("cell phone")) {
                parts[i] = faker.phoneNumber().cellPhone();
            }
            else if (typeInput[i].contains("phone")) {
                parts[i] = faker.phoneNumber().phoneNumber();
            }
            else {
                parts[i] = "INVALID TYPE";
            }
        }
        return parts;
    }
    public static String[] createQueries() throws ParseException {
        int size = readInt("How many rows do you want to generate?");
        String[] types = typeInput();
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = createQueryFormat(generateRow(types));
        }
        return result;
    }
    public static void printQueries() throws ParseException {
        String tableName = readString("What is the name of your table?", ".+");
        String[] queries = createQueries();
        String header = String.format("INSERT INTO %s VALUES\n", tableName);
        System.out.print(header);
        for (int i = 0; i < queries.length - 1; i++) {
            String line = "\t" + queries[i] + ",\n";
            System.out.print(line);
        }
        String line = "\t" + queries[queries.length - 1] + ";\n";
        System.out.print(line);
    }
    public static void createFile(String filename) {
        try {
            File myObj = new File(filename + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void writeToFile() {
        String filename = readString("What is the name of your file?",".+");
        try {
            createFile(filename);
            FileWriter myWriter = new FileWriter(filename + ".txt");

            String tableName = readString("What is the name of your table?", ".+");
            String[] queries = createQueries();
            String header = String.format("INSERT INTO %s VALUES\n", tableName);
            myWriter.write(header);
            for (int i = 0; i < queries.length - 1; i++) {
                String line = "\t" + queries[i] + ",\n";
                myWriter.write(line);
            }
            String line = "\t" + queries[queries.length - 1] + ";\n";
            myWriter.write(line);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}