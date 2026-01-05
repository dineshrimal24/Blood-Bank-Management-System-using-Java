package projectiv;

public class Donor {
    private final int id;
    private final String name;
    private final String fatherName;
    private final String dateOfBirth; // NEW: DOB field
    private final int age;
    private final String gender;
    private final String bloodGroup;
    private final String phone;
    private final String donatedCity;
    private final String lastDonationDate;
    private final String permanentAddress;
    private final String temporaryAddress;

    // NEW: Constructor with DOB
    public Donor(int id, String name, String fatherName, String dateOfBirth, int age, String gender,
                 String bloodGroup, String phone, String donatedCity,
                 String lastDonationDate, String permanentAddress, String temporaryAddress) {
        this.id = id;
        this.name = name;
        this.fatherName = fatherName;
        this.dateOfBirth = dateOfBirth == null ? "" : dateOfBirth;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.donatedCity = donatedCity;
        this.lastDonationDate = lastDonationDate == null ? "" : lastDonationDate;
        this.permanentAddress = permanentAddress == null ? "" : permanentAddress;
        this.temporaryAddress = temporaryAddress == null ? "" : temporaryAddress;
    }

    // OLD: Constructor without DOB (for backward compatibility)
    public Donor(int id, String name, String fatherName, int age, String gender,
                 String bloodGroup, String phone, String donatedCity,
                 String lastDonationDate, String permanentAddress, String temporaryAddress) {
        this(id, name, fatherName, "", age, gender, bloodGroup, phone, donatedCity,
                lastDonationDate, permanentAddress, temporaryAddress);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getFatherName() { return fatherName; }
    public String getDateOfBirth() { return dateOfBirth; } // NEW getter
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getBloodGroup() { return bloodGroup; }
    public String getPhone() { return phone; }
    public String getDonatedCity() { return donatedCity; }
    public String getLastDonationDate() { return lastDonationDate; }
    public String getPermanentAddress() { return permanentAddress; }
    public String getTemporaryAddress() { return temporaryAddress; }

    /**
     * Returns a formatted donor ID string (e.g., "D0001", "D0042", "D0123")
     * This is useful for display purposes in reports and UI
     *
     * @return Formatted donor ID string with "D" prefix and 4-digit number
     */
    public String getDonorId() {
        return "D" + String.format("%04d", id);
    }
}