import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) {
        SplayTree dictionary = new SplayTree();
    
        // Read words from a text file and insert them into the dictionary
        try (BufferedReader dictFile = new BufferedReader(new FileReader("/Volumes/StudyorWork/ACC/Hotel-price-analysis-scraping/splayRegex/cities.txt"))) {
            String line;
            while ((line = dictFile.readLine()) != null) {
                dictionary.insert(line.toLowerCase()); // Converting all the words to Lowercase to make it case insensitive.
            }
        } catch (IOException e) {
            System.err.println("Error reading dictionary file: " + e.getMessage());
            return; // Terminate the program if dictionary file cannot be read
        }
    
        boolean cityFound = false;
        try (Scanner scanner = new Scanner(System.in)) {
           
            String city="";
    
            while (!cityFound) {
                // Get the user input string to do the spell check
                // Converting the user input to Lowercase to match it with the dictionary values.
                city = getInput(scanner, "Enter city: ", "[a-zA-Z\\s]+").trim().toLowerCase(); // Trim to remove leading and trailing whitespaces
    
                // setting the editDistance threshold to 3
                int maxDistance = 2;
    
                if (dictionary.get(city) > 0) {
                    System.out.println("City is spelled correctly.");
                    cityFound = true;
                } else {
                    List<String> similarCities = dictionary.similarWords(city, maxDistance);
                    if (!similarCities.isEmpty()) {
                        System.out.println("City not found. Do you mean:");
                        for (String similarCity : similarCities) {
                            System.out.println(similarCity);
                        }
                        System.out.println("Please type the city again:");
                    } else {
                        System.out.println("City not found. No suggestions available.");
                        System.out.println("Please type the city again:");
                    }
                }
            }
    
            String fromDateStr;
            String toDateStr;
    
            // Input for from date
            LocalDate fromDate;
            do {
                fromDateStr = getInput(scanner, "Enter from date (dd Month yyyy): ", "\\d{2}\\s+(January|February|March|April|May|June|July|August|September|October|November|December|january|february|march|april|may|june|july|august|september|october|november|december|JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER\n" + //
                                        ")\\s+\\d{4}");
                fromDate = parseDate(fromDateStr);
                if (fromDate == null || fromDate.isBefore(LocalDate.now())) {
                    System.out.println("Invalid input. From date should be today or a future date.");
                }
            } while (fromDate == null || fromDate.isBefore(LocalDate.now()));
    
            // Input for to date
            LocalDate toDate;
            do {
                toDateStr = getInput(scanner, "Enter to date (dd Month yyyy): ", "\\d{2}\\s+(January|February|March|April|May|June|July|August|September|October|November|December|january|february|march|april|may|june|july|august|september|october|november|december|JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER\n" + //
                                        ")\\s+\\d{4}");
                toDate = parseDate(toDateStr);
                if (toDate == null || toDate.isBefore(fromDate)) {
                    System.out.println("Invalid input. To date should be after from date.");
                }
            } while (toDate == null || toDate.isBefore(fromDate));
    
            // Input for number of rooms
            int numRooms;
            while (true) {
                try {
                    String numRoomsStr = getInput(scanner, "Enter number of rooms: ", "[1-9]");
                    numRooms = Integer.parseInt(numRoomsStr);
                    break; // Break the loop if input is successfully parsed
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }
    
            // Input for number of adults
            int numAdults;
            while (true) {
                try {
                    String numAdultsStr = getInput(scanner, "Enter number of adults: ", "[1-9]");
                    numAdults = Integer.parseInt(numAdultsStr);
                    break; // Break the loop if input is successfully parsed
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }
    
            // Display the inputs
            System.out.println("City: " + city);
            System.out.println("From Date: " + fromDateStr);
            System.out.println("To Date: " + toDateStr);
            System.out.println("Number of Rooms: " + numRooms);
            System.out.println("Number of Adults: " + numAdults);
        }
    }
    
    // Method to get input and validate using regex
    private static String getInput(Scanner scanner, String prompt, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                System.out.println("Invalid input. Please try again.");
            }
        } while (!matcher.matches());
        return input;
    }

    // Method to parse date string into LocalDate
    private static LocalDate parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd MMMM yyyy")
                    .toFormatter(Locale.ENGLISH);
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "App []";
    }
}