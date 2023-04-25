package thomas.plantfiches;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class PlantRecord implements Comparable<PlantRecord> {

    public String abbrev;
    public int amount;
    public String englishName;
    public String scientificName;

    public PlantRecord(String abbrev, int amount, String scientificName, String englishName) {
        this.abbrev = abbrev;
        this.amount = amount;
        this.scientificName = scientificName;
        this.englishName = englishName;
    }

    public void addToList(List<PlantRecord> list) {
        boolean merged = false;
        for (PlantRecord p : list) {
            if (p.scientificName.equals(this.scientificName)) {
                p.amount = p.amount + this.amount;
                merged = true;
            }
        }
        if (!merged) {
            list.add(this);
        }
    }

    @Override
    public String toString() {
        return scientificName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantRecord that = (PlantRecord) o;
        return amount == that.amount && Objects.equals(abbrev, that.abbrev) && Objects.equals(englishName, that.englishName) && scientificName.equals(that.scientificName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abbrev, amount, englishName, scientificName);
    }

    public String subscript() {
        return scientificName + " (" + abbrev + ": " + amount + ")";
    }

    @Override
    public int compareTo(PlantRecord o) {
        return this.scientificName.compareTo(o.scientificName);
    }

    public boolean fileNameFits(Path file) {
        return this.scientificName.replace("’", "").replace("‘", "").replace(" x ", " ").replace("'", "").trim().toLowerCase().equals(filenameToScientificNameLC(file.getFileName().toString()));
    }

    private static String filenameToScientificNameLC(String name) {
        return name.replace(".jpeg", "").replace(".jpg", "").replace(".JPEG", "").replace("_", " ").trim().toLowerCase();
    }

    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


}
